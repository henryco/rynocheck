package net.henryco.rynocheck.transaction;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import lombok.val;
import net.henryco.rynocheck.data.dao.balance.MoneyBalanceDao;
import net.henryco.rynocheck.data.dao.transaction.MoneyTransactionDao;
import net.henryco.rynocheck.data.model.MoneyBalance;
import net.henryco.rynocheck.data.model.MoneyTransaction;
import net.henryco.rynocheck.data.page.Page;
import net.henryco.rynocheck.transaction.exec.ITransactionExecutor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.henryco.rynocheck.data.model.MoneyTransaction.TAG_FEE;

@Component @Singleton @Log
public class MoneyTransactionService implements IMoneyTransactionService {

	private static final long QUERY_PAGE_SIZE = 10000;

	private final ITransactionExecutor transactionExecutor;
	private final MoneyTransactionDao transactionDao;
	private final MoneyBalanceDao balanceDao;

	@Inject
	public MoneyTransactionService(ITransactionExecutor transactionExecutor,
								   MoneyTransactionDao transactionDao,
								   MoneyBalanceDao balanceDao) {
		this.transactionExecutor = transactionExecutor;
		this.transactionDao = transactionDao;
		this.balanceDao = balanceDao;
	}


	@Override
	public void calculateTransactions(String user, Long currency, BiConsumer<BigDecimal, Throwable> amountConsumer) {
		transactionExecutor.submit(currency, () -> calculate(user, currency, amountConsumer));
	}

	@Override
	public void releaseTransaction(MoneyTransaction transaction, Notification notification) {
		transactionExecutor.submit(transaction.getCurrency(), () -> releaseTransaction(transaction, notification, false, true));
	}

	@Override
	public void releaseTransaction(MoneyTransaction transaction, Notification notification, boolean microEnabled) {

		if (microEnabled) {

			val microLimit = transaction.getTransactional().getMicroLimit();
			val amount = transaction.getAmount();
			if (microLimit == null || amount.compareTo(microLimit) <= 0) {
				releaseMicroTransaction(transaction, notification);
				return;
			}
		}

		releaseTransaction(transaction, notification);
	}

	@Override
	public void releaseEmit(MoneyTransaction transaction, Notification notification) {
		transactionExecutor.submit(transaction.getCurrency(), () -> releaseTransaction(transaction, notification, true, false));
	}

	@Override
	public void releaseMicroTransaction(MoneyTransaction transaction, Notification notification) {

		val amount = transaction.getAmount();
		val sender = transaction.getSender();
		val recipient = transaction.getReceiver();
		val currencyCode = transaction.getCurrencyCode();
		val currency = transaction.getCurrency();

		MoneyBalance senderBalance = balanceDao.getUserBalance(sender, currency);
		MoneyBalance receiverBalance = balanceDao.getUserBalance(recipient, currency);
		if (receiverBalance == null) {
			receiverBalance = balanceDao.createNewOne(recipient, currency);
		}

		val rBalanceAmount = receiverBalance.getAmount();
		val sBalanceAmount = senderBalance.getAmount();
		if (sBalanceAmount.compareTo(amount) < 0) {
			val deficit = sBalanceAmount.subtract(amount).abs().toString();
			notification.error(sender, deficit, currencyCode);
			return;
		}

		receiverBalance.setAmount(rBalanceAmount.add(amount));
		senderBalance.setAmount(sBalanceAmount.subtract(amount));

		balanceDao.updateBalance(receiverBalance);
		balanceDao.updateBalance(senderBalance);

		notification.success(sender, senderBalance.getAmount().toString(), currencyCode);
		notification.success(recipient, receiverBalance.getAmount().toString(), currencyCode);

		this.releaseTransaction(transaction, notification);
	}


	@Data
	@AllArgsConstructor
	private static final class ProcessBundle {
		private final MoneyTransaction transaction;
		private final Notification notification;
	}


	private void calculate(String user, Long currency, BiConsumer<BigDecimal, Throwable> amountConsumer) {

		try {

			BigDecimal value = new BigDecimal(0);
			long page = 0;

			while (true) {

				List<MoneyTransaction> transactions = transactionDao.getUserTransactions(
						user, currency, Page.factory.page(page++, QUERY_PAGE_SIZE)
				);

				if (transactions == null || transactions.isEmpty())
					break;

				for (MoneyTransaction transaction : transactions) {
					if (user.equals(transaction.getSender()))
						value = value.subtract(transaction.getAmount());
					if (user.equals(transaction.getReceiver()))
						value = value.add(transaction.getAmount());
				}
			}

			amountConsumer.accept(value, null);

		} catch (Exception e) {
			amountConsumer.accept(null, e);
		}
	}


	private void releaseTransaction(MoneyTransaction transaction, Notification notification,
									boolean force, boolean fee) {
		log.info(" ");
		log.info("-------------------");
		log.info("releaseTransaction(t: " + transaction +", n: " + notification + ", force: " + force + ", fee: " + fee);

		val bundle = new ProcessBundle(transaction, notification);
		val sender = transaction.getSender();
		val receiver = transaction.getReceiver();

		if (transaction.getAmount().compareTo(new BigDecimal(0)) == 0) {
			log.info("Transaction amount is 0, skipping redundant calculations");
			return;
		}

		log.info("SRC: " + sender);
		log.info("DST: " + receiver);
		log.info("bundle: " + bundle);


		log.info(":::PART SENDER::: START");
		processSenderResult(sender, bundle, force);
		log.info(":::PART SENDER::: END");


		log.info(":::TRANSACTION::: SAVING");
		transactionDao.saveTransaction(transaction);
		log.info(":::TRANSACTION::: DONE");


		log.info(":::PART FEE::: STARTING");
		processFee(bundle, fee); // It's also async, coz, who rly cares about fees?
		log.info(":::PART FEE::: STARTED");


		log.info(":::PART RECEIVER::: START");
		processReceiverResult(bundle);
		log.info(":::PART RECEIVER::: END");

	}


	private void processReceiverResult(ProcessBundle bundle) {

		val receiver = bundle.transaction.getReceiver();
		val balance = balanceDao.getOrCrateUserBalance(receiver, bundle.transaction.getCurrency());

		processCalculatedResult(receiver, balance, bundle, null);
	}


	private void processSenderResult(String sender, ProcessBundle bundle, boolean force) {

		val balance = balanceDao.getOrCrateUserBalance(sender, bundle.transaction.getCurrency());
		processCalculatedResult(sender, balance, bundle, senderResult -> {

			BigDecimal amount = bundle.transaction.getAmount();
			BigDecimal finalAmount = senderResult.subtract(amount.abs());
			BigDecimal microLimit = bundle.transaction.getTransactional().getMicroLimit();

			log.info("amount: " + amount);
			log.info("microLimit: " + microLimit);
			log.info("finalAmount: " + finalAmount);

			if (finalAmount.compareTo(microLimit.negate()) < 0) {
				if (!force) {
					bundle.notification.error(sender, senderResult.toString(), bundle.transaction.getCurrencyCode());
					return null;
				}
			}
			return finalAmount;
		});
	}


	private void processCalculatedResult(String user, MoneyBalance balance, ProcessBundle bundle,
										 Function<BigDecimal, BigDecimal> resultFunction) {

		val currency = bundle.transaction.getCurrency();
		val currencyCode = bundle.transaction.getCurrencyCode();

		this.calculate(user, currency, (result, throwable) -> {
			log.info("Calculated: " + user + ", " + currency + ", " + result + ", " + throwable);

			if (throwable != null) {
				onThrow(throwable);
				return;
			}

			BigDecimal finalAmount = result;
			if (resultFunction != null) {
				finalAmount = resultFunction.apply(result);
				if (finalAmount == null)
					return;
			}

			updateBalanceIfNeed(balance, finalAmount, currencyCode, bundle.notification);
		});
	}


	private void updateBalanceIfNeed(MoneyBalance balance, BigDecimal finalAmount,
									 String currencyCode, Notification notification) {

		log.info("Update balance: " + balance.getUser());
		log.info("Balance before: " + balance);
		if (finalAmount != null && finalAmount.compareTo(balance.getAmount()) != 0) {

			log.info("setAmount(" + finalAmount + ")");

			balance.setAmount(finalAmount);
			balance.setLastUpdate(new Date(System.currentTimeMillis()));

			if (!balanceDao.updateBalance(balance)) {
				log.info("updateBalance error");
				notification.error(balance.getUser(), finalAmount.toString(), currencyCode);
			}
			notification.success(balance.getUser(), finalAmount.toString(), currencyCode);
		}
		log.info("Balance after: " + balance);
	}


	private void processFee(ProcessBundle bundle, boolean fee) {

		val feeTransaction = createFee(bundle.transaction);
		log.info("transaction: " + feeTransaction);
		if (feeTransaction != null && fee) {
			log.info("Releasing transaction into new thread...");
			this.releaseEmit(feeTransaction, bundle.notification);
		}
	}


	private static MoneyTransaction createFee(MoneyTransaction transaction) {

		val transactional = transaction.getTransactional();
		val fee = transactional.getFee();

		if (fee == null || fee.equals(new BigDecimal(0)))
			return null;

		MoneyTransaction transactionFee = new MoneyTransaction();
		transactionFee.setAmount(transaction.getAmount().multiply(fee));
		transactionFee.setReceiver(transactional.getEmitter());
		transactionFee.setSender(transaction.getSender());
		transactionFee.setTime(transaction.getTime());
		transactionFee.setCurrency(transactional);
		transactionFee.setDescription(TAG_FEE);
		return transactionFee;
	}


	private static void onThrow(Throwable throwable) {
		throwable.printStackTrace();
		log.throwing(MoneyTransactionService.class.getName(),
				"calculateTransactions", throwable
		);
	}

}
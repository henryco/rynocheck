package net.henryco.rynocheck.service;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.extern.java.Log;
import lombok.val;
import net.henryco.rynocheck.data.dao.balance.MoneyBalanceDao;
import net.henryco.rynocheck.data.dao.transaction.MoneyTransactionDao;
import net.henryco.rynocheck.data.model.MoneyTransaction;
import net.henryco.rynocheck.data.page.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

import static net.henryco.rynocheck.data.model.MoneyTransaction.TAG_FEE;

@Component @Singleton @Log
public class MoneyTransactionService implements IMoneyTransactionService {

	private static final long QUERY_PAGE_SIZE = 10000;

	private final ExecutorService executorService;
	private final MoneyTransactionDao transactionDao;
	private final MoneyBalanceDao balanceDao;

	@Inject
	public MoneyTransactionService(ExecutorService executorService,
								   MoneyTransactionDao transactionDao,
								   MoneyBalanceDao balanceDao) {
		this.executorService = executorService;
		this.transactionDao = transactionDao;
		this.balanceDao = balanceDao;
	}


	@Override
	public void calculateTransactions(String user, Long currency,
									  BiConsumer<BigDecimal, Throwable> amountConsumer) {
		executorService.submit(() -> {
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
						value = transaction.getSender().equals(user)
								? value.subtract(transaction.getAmount())
								: value.add(transaction.getAmount());
					}
				}

				amountConsumer.accept(value, null);

			} catch (Exception e) {
				amountConsumer.accept(null, e);
			}
		});
	}


	@Override
	public void releaseTransaction(MoneyTransaction transaction, Notification notification) {
		releaseTransaction(transaction, notification, false, true);
	}

	@Override
	public void releaseEmit(MoneyTransaction transaction, Notification notification) {
		releaseTransaction(transaction, notification, true, false);
	}


	private void releaseTransaction(MoneyTransaction transaction,
									Notification notification,
									boolean force,
									boolean fee) {
		log.info(" ");
		log.info("-------------------");
		log.info("releaseTransaction(t: " + transaction +", n: " + notification + ", force: " + force + ", fee: " + fee);

		val sender = transaction.getSender();
		val receiver = transaction.getReceiver();

		if (sender.equals(receiver)) {
			log.info("SENDER == RECEIVER, skipping redundant calculations");
			return;
		}

		val currency = transaction.getCurrency();
		val code = transaction.getCurrencyCode();
		val amount = transaction.getAmount().abs();
		val microLimit = transaction.getTransactional().getMicroLimit();

		val rBalance = balanceDao.getOrCrateUserBalance(receiver, currency);
		val sBalance = balanceDao.getOrCrateUserBalance(sender, currency);


		log.info("sender: " + sender);
		log.info("receiver: " + receiver);
		log.info("currency: " + currency);
		log.info("code: " + code);
		log.info("amount: " + amount);
		log.info("microLimit: " + microLimit);
		log.info("rBalance: " + rBalance);
		log.info("sBalance: " + sBalance);


		this.calculateTransactions(sender, currency, (senderResult, senderThrowable) -> {


			log.info(":::PART SENDER::: START");
			log.info("Calculated: " + sender + ", " + currency + ", " + senderResult + ", " + senderThrowable);

			if (senderThrowable != null) {
				onThrow(senderThrowable);
				return;
			}

			BigDecimal finalSender = senderResult.subtract(amount);
			log.info("finalSender: " + finalSender);

			if (finalSender.compareTo(microLimit.negate()) < 0) {
				if (!force) {
					notification.error(sender, senderResult.toString(), code);
					return;
				}
			}

			log.info(":::PART SENDER::: END");

			this.calculateTransactions(receiver, currency, (receiverResult, receiverThrowable) -> {

				log.info(":::PART RECEIVER::: START");
				log.info("Calculated: " + sender + ", " + currency + ", " + receiverResult + ", " + receiverThrowable);

				if (receiverThrowable != null) {
					onThrow(receiverThrowable);
					return;
				}

				log.info(":::PART RECEIVER::: END");


				BigDecimal finalReceiver = receiverResult.add(amount);
				log.info("finalReceiver: " + finalReceiver);


				transactionDao.saveTransaction(transaction);
				log.info("transaction saved");


				log.info("Sender balance update");
				if (finalSender.compareTo(sBalance.getAmount()) != 0) {

					log.info("setAmount(" + finalSender + ")");

					sBalance.setAmount(finalSender);
					if (!balanceDao.updateBalance(sBalance)) {
						log.info("updateBalance error");
						notification.error(sender, senderResult.toString(), code);
					}

					notification.success(sender, finalSender.toString(), code);
				}

				log.info("Receiver balance update");
				if (finalReceiver != null && finalReceiver.compareTo(rBalance.getAmount()) != 0) {
					log.info("setAmount(" + finalReceiver + ")");
					rBalance.setAmount(finalReceiver);
					if (!balanceDao.updateBalance(rBalance)) {
						log.info("updateBalance error");
						notification.error(receiver, finalReceiver.toString(), code);
					}
					notification.success(receiver, finalReceiver.toString(), code);
				}


				MoneyTransaction feeTransaction = createFee(transaction);
				log.info("feeTransaction: " + feeTransaction);
				if (feeTransaction != null && fee) {

					this.releaseEmit(feeTransaction, notification);
					log.info("fee released");
				}

			});

		});

	}



	private static void onThrow(Throwable throwable) {
		throwable.printStackTrace();
		log.throwing(MoneyTransactionService.class.getName(),
				"calculateTransactions", throwable
		);
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

}
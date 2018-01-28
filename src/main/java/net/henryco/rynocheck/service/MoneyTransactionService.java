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

@Component @Singleton @Log
public class MoneyTransactionService implements IMoneyTransactionService {

	private static final long QUERY_PAGE_SIZE = 10000;
	private static final String TAG_FEE = "fee";

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
		log.info("-------------------");
		log.info("releaseTransaction(t: " + transaction +", n: " + notification + ", force: " + force + ", fee: " + fee);

		val sender = transaction.getSender();
		val receiver = transaction.getReceiver();
		val currency = transaction.getCurrency();
		val code = transaction.getCurrencyCode();
		val amount = transaction.getAmount();
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



		this.calculateTransactions(sender, currency, (result, throwable) -> {
			log.info(":::PART SENDER::: START");
			log.info("Calculated: " + sender + ", " + currency + ", " + result + ", " + throwable);

			if (throwable != null) {
				onThrow(throwable);
				return;
			}

			BigDecimal finalResult = result.subtract(amount);
			if (finalResult.compareTo(microLimit.negate()) < 0) {
				if (!force) {
					notification.error(sender, result.toString(), code);
					return;
				}
			}

			log.info("finalResult: " + finalResult);

			MoneyTransaction feeTransaction = createFee(transaction);
			log.info("feeTransaction: " + feeTransaction);
			if (feeTransaction != null && fee) {
				finalResult = finalResult.subtract(feeTransaction.getAmount());
				transactionDao.saveTransaction(feeTransaction);
				log.info("fee saved");
			}

			transactionDao.saveTransaction(transaction);
			log.info("transaction saved");

			if (finalResult.compareTo(sBalance.getAmount()) != 0) {

				log.info("setAmount(" + finalResult + ")");

				sBalance.setAmount(finalResult);
				if (!balanceDao.updateBalance(sBalance)) {
					log.info("updateBalance error");
					notification.error(sender, result.toString(), code);
				}

				notification.success(sender, finalResult.toString(), code);
			}
			log.info(":::PART SENDER::: END");
		});


		this.calculateTransactions(receiver, currency, (result, throwable) -> {
			log.info(":::PART RECEIVER::: START");
			log.info("Calculated: " + sender + ", " + currency + ", " + result + ", " + throwable);

			if (throwable != null) {
				onThrow(throwable);
				return;
			}

			if (result != null && result.compareTo(rBalance.getAmount()) != 0) {
				log.info("setAmount(" + result + ")");
				rBalance.setAmount(result);
				if (!balanceDao.updateBalance(rBalance)) {
					log.info("updateBalance error");
					notification.error(receiver, result.toString(), code);
				}
				notification.success(receiver, result.toString(), code);
			}
			log.info(":::PART RECEIVER::: END");
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
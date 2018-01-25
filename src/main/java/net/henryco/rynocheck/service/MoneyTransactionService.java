package net.henryco.rynocheck.service;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.data.dao.transaction.MoneyTransactionDao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

@Component @Singleton
public class MoneyTransactionService implements IMoneyTransactionService {

	private final ExecutorService executorService;
	private final MoneyTransactionDao transactionDao;

	@Inject
	public MoneyTransactionService(ExecutorService executorService,
								   MoneyTransactionDao transactionDao) {
		this.executorService = executorService;
		this.transactionDao = transactionDao;
	}


	@Override
	public void calculateTransactions(String user, Currency currency,
									  BiConsumer<BigDecimal, Throwable> amountConsumer) {
		executorService.submit(() -> {
			try {
				List<MoneyTransaction> transactions = transactionDao.getUserTransactions(user, currency);
				BigDecimal value = new BigDecimal(0);
				for (MoneyTransaction transaction : transactions) {

					value = transaction.getSender().equals(user)
							? value.subtract(transaction.getAmount())
							: value.add(transaction.getAmount());
				}

				amountConsumer.accept(value, null);

			} catch (Exception e) {
				amountConsumer.accept(null, e);
			}
		});
	}

	

}
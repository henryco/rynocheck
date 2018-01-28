package net.henryco.rynocheck.service;


import net.henryco.rynocheck.data.model.MoneyTransaction;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

public interface IMoneyTransactionService {

	void releaseTransaction(MoneyTransaction transaction, Notification notification);

	void releaseEmit(MoneyTransaction transaction, Notification notification);

	interface Notification {
		void success(String userName, String amount, String currency);
		void error(String userName, String amount, String currency);
	}

	void calculateTransactions(String user, Long currency,
							   BiConsumer<BigDecimal, Throwable> amountConsumer
	);
}

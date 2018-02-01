package net.henryco.rynocheck.transaction;


import net.henryco.rynocheck.data.model.MoneyTransaction;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

public interface IMoneyTransactionService {

	void releaseTransaction(MoneyTransaction transaction, Notification notification);

	void releaseTransaction(MoneyTransaction transaction, Notification notification, boolean micro);

	void releaseEmit(MoneyTransaction transaction, Notification notification);

	void releaseMicroTransaction(MoneyTransaction transaction, Notification notification);

	interface Notification {
		void success(String userName, String amount, String currency);
		void error(String userName, String amount, String currency);
	}

	void calculateTransactions(String user, Long currency, BiConsumer<BigDecimal, Throwable> amountConsumer);
}

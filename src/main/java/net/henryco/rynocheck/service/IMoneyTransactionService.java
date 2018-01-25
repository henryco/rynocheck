package net.henryco.rynocheck.service;

import net.henryco.rynocheck.data.model.Currency;

import java.math.BigDecimal;
import java.util.function.BiConsumer;

public interface IMoneyTransactionService {
	void calculateTransactions(String user, Currency currency,
							   BiConsumer<BigDecimal, Throwable> amountConsumer);
}

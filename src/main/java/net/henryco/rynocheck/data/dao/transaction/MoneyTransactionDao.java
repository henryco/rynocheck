package net.henryco.rynocheck.data.dao.transaction;

import com.j256.ormlite.dao.Dao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyTransaction;
import net.henryco.rynocheck.data.page.Page;

import java.util.List;

public interface MoneyTransactionDao extends Dao<MoneyTransaction, Long> {

	List<MoneyTransaction> getUserTransactions(String user);

	List<MoneyTransaction> getUserTransactions(String user, Page page);

	List<MoneyTransaction> getUserTransactions(String user, Currency currency);

	List<MoneyTransaction> getUserTransactions(String user, Currency currency, Page page);

	boolean saveTransaction(MoneyTransaction transaction);
}
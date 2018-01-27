package net.henryco.rynocheck.data.dao.transaction;

import com.j256.ormlite.dao.Dao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyTransaction;

import java.util.List;

public interface MoneyTransactionDao extends Dao<MoneyTransaction, Long> {

	List<MoneyTransaction> getUserTransactions(String user);

	List<MoneyTransaction> getUserTransactions(String user, Currency currency);

	boolean saveTransaction(MoneyTransaction transaction);
}
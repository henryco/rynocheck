package net.henryco.rynocheck.data.dao.balance;

import com.j256.ormlite.dao.Dao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyBalance;
import net.henryco.rynocheck.data.page.DaoPage;

import java.util.List;

public interface MoneyBalanceDao extends Dao<MoneyBalance, Long> {

	MoneyBalance createNewOne(String user, Currency currency);

	List<MoneyBalance> getUserBalanceList(String user);

	List<MoneyBalance> getUserBalanceList(String user, DaoPage page);

	MoneyBalance getUserBalance(String user, Currency currency);
}
package net.henryco.rynocheck.data.dao.balance;

import com.j256.ormlite.dao.Dao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyBalance;
import net.henryco.rynocheck.data.page.Page;

import java.util.List;

public interface MoneyBalanceDao extends Dao<MoneyBalance, Long> {

	MoneyBalance createNewOne(String user, Long currency);

	List<MoneyBalance> getUserBalanceList(String user);

	List<MoneyBalance> getUserBalanceList(String user, Page page);

	MoneyBalance getUserBalance(String user, Long currency);

	MoneyBalance getOrCrateUserBalance(String user, Long currency);

	boolean updateBalance(MoneyBalance balance);
}
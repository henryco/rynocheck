package net.henryco.rynocheck.data.dao.wallet;

import com.j256.ormlite.dao.Dao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyBalance;

import java.util.List;

public interface MoneyBalanceDao extends Dao<MoneyBalance, Long> {

	List<MoneyBalance> getUserBalanceList(String user);

	MoneyBalance getUserBalance(Currency currency, String user);
}
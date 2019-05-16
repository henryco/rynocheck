package net.henryco.rynocheck.data.dao.account;

import com.j256.ormlite.dao.Dao;
import net.henryco.rynocheck.data.model.MoneyAccount;

public interface MoneyAccountDao extends Dao<MoneyAccount, String> {

	boolean isEmailExists(String email);

	boolean isAccountExistsByUsernameAndPasswordHash(String name, String password);

	boolean isUsernameExists(String name);

	MoneyAccount getAccountByUsername(String name);
}

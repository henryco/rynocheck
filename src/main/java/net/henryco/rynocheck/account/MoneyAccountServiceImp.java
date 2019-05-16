package net.henryco.rynocheck.account;

import com.github.henryco.injector.meta.annotations.Provide;
import lombok.val;
import net.henryco.rynocheck.data.dao.account.MoneyAccountDao;
import net.henryco.rynocheck.data.model.MoneyAccount;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Calendar;

@Provide @Singleton
public class MoneyAccountServiceImp implements MoneyAccountService {

	private static final class Helper {
		private static boolean matches(String input) {
			return input != null && input.length() >= 5 && input.matches("[A-Za-z0-9]*");
		}
		private static String cryptWithMD5(String pass) {
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.reset();

				StringBuilder sb = new StringBuilder();
				for (byte b : md.digest(pass.getBytes()))
					sb.append(Integer.toHexString(0xff & b));
				return sb.toString();
			} catch (NoSuchAlgorithmException ex) {
				throw new RuntimeException("Cannot encode user password", ex);
			}
		}
	}

	private final MoneyAccountDao moneyAccDao;

	@Inject
	public MoneyAccountServiceImp(MoneyAccountDao moneyAccounts) {
		this.moneyAccDao = moneyAccounts;
	}

	@Override
	public void addAccount(String username, String password, String email) throws MoneyAccountException {
		val account = new MoneyAccount();

		try {
			if (moneyAccDao.idExists(username.trim())) {
				throw new MoneyAccountException("This name is already taken!");
			} else account.setUid(username);

			if (password == null || password.trim().isEmpty()) {
				throw new MoneyAccountException("Password required");
			} else account.setPass(Helper.cryptWithMD5(password));

			if (email != null && moneyAccDao.isEmailExists(email)) {
				throw new MoneyAccountException("This email is already taken!");
			} else if (email != null) account.setEmail(email);

			if (!Helper.matches(username)) {
				throw new MoneyAccountException("Name should be at least 5 characters length, A-Z 0-9");
			}

			account.setLastUpdate(Calendar.getInstance().getTime());
			moneyAccDao.create(account);
		} catch (SQLException e) {
			throw new RuntimeException("Cannot save user account: ", e);
		}
	}

	@Override
	public boolean authenticate(String name, String password) {
		return moneyAccDao.isAccountExistsByUsernameAndPasswordHash(name, Helper.cryptWithMD5(password));
	}

	@Override
	public Account getAccount(String name) throws MoneyAccountException {
		val account = moneyAccDao.getAccountByUsername(name);
		if (account == null) throw new MoneyAccountException("Account does not exists: " + name);
		return new Account(account.getUid(), account.getDescription(), account.getEmail(), account.getLastUpdate());
	}
}
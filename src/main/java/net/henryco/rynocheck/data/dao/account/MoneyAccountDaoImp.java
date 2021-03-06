package net.henryco.rynocheck.data.dao.account;


import com.github.henryco.injector.meta.annotations.Provide;
import com.j256.ormlite.support.ConnectionSource;
import net.henryco.rynocheck.data.dao.RynoCheckDao;
import net.henryco.rynocheck.data.model.MoneyAccount;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;

import static net.henryco.rynocheck.data.model.MoneyAccount.ACCOUNT_ID;
import static net.henryco.rynocheck.data.model.MoneyAccount.PASSWORD;


@Provide @Singleton
public class MoneyAccountDaoImp extends RynoCheckDao<MoneyAccount, String>
		implements MoneyAccountDao {


	@Inject
	public MoneyAccountDaoImp(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MoneyAccount.class);
	}

	@Override
	public boolean isEmailExists(String email) {

		if (!assertString(email)) return false;
		try {
			MoneyAccount account = queryBuilder().where().eq(ACCOUNT_ID, email.trim()).queryForFirst();
			return account != null;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public boolean isAccountExistsByUsernameAndPasswordHash(String name, String password) {

		if (name == null || password == null) return false;

		try {
			return queryBuilder().where().eq(ACCOUNT_ID, name).and()
					.eq(PASSWORD, password).queryForFirst() != null;
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public boolean isUsernameExists(String name) {

		if (!assertString(name)) return false;
		try {
			return idExists(name);
		} catch (SQLException e) {
			return false;
		}
	}

	@Override
	public MoneyAccount getAccountByUsername(String name) {

		if (!assertString(name)) return null;
		try {
			return queryForId(name);
		} catch (SQLException e) {
			return null;
		}
	}
}
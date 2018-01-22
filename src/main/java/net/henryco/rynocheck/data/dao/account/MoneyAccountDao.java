package net.henryco.rynocheck.data.dao.account;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import net.henryco.rynocheck.data.model.entity.MoneyAccount;

import java.sql.SQLException;

import static net.henryco.rynocheck.data.model.entity.MoneyAccount.ACCOUNT_ID;
import static net.henryco.rynocheck.data.model.entity.MoneyAccount.PASSWORD;

public class MoneyAccountDao extends BaseDaoImpl<MoneyAccount, String> {


	public MoneyAccountDao(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MoneyAccount.class);
	}

	public boolean isEmailExists(String email) {

		if (email == null || email.trim().isEmpty()) return false;
		try {
			MoneyAccount account = queryBuilder().where().eq(ACCOUNT_ID, email.trim()).queryForFirst();
			return account != null;
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean authenticate(String name, String password) {

		if (name == null || password == null) return false;

		try {
			return queryBuilder().where().eq(ACCOUNT_ID, name).and()
					.eq(PASSWORD, password).queryForFirst() != null;
		} catch (SQLException e) {
			return false;
		}
	}

}
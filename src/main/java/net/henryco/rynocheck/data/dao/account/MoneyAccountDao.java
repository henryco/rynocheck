package net.henryco.rynocheck.data.dao.account;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import net.henryco.rynocheck.data.model.entity.MoneyAccount;

import java.sql.SQLException;

public class MoneyAccountDao extends BaseDaoImpl<MoneyAccount, String> {


	public MoneyAccountDao(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MoneyAccount.class);
	}

	public boolean isEmailExists(String email) {

		if (email == null || email.trim().isEmpty()) return false;
		try {
			MoneyAccount account = queryBuilder().where().eq("account_id", email.trim()).queryForFirst();
			return account != null;
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean authenticate(String name, String password) {

		if (name == null || password == null) return false;

		try {
			return queryBuilder().where().eq("account_id", name).and()
					.eq("password", password).queryForFirst() != null;
		} catch (SQLException e) {
			return false;
		}
	}

}
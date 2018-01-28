package net.henryco.rynocheck.data.dao.balance;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import com.j256.ormlite.support.ConnectionSource;
import lombok.extern.java.Log;
import net.henryco.rynocheck.data.dao.RynoCheckDao;
import net.henryco.rynocheck.data.model.MoneyBalance;
import net.henryco.rynocheck.data.page.Page;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static net.henryco.rynocheck.data.model.MoneyBalance.ACCOUNT_ID;
import static net.henryco.rynocheck.data.model.MoneyBalance.CURRENCY;
import static net.henryco.rynocheck.data.model.MoneyBalance.ID;

@Component @Singleton @Log
public class MoneyBalanceDaoImp extends RynoCheckDao<MoneyBalance, Long> implements MoneyBalanceDao {

	@Inject
	public MoneyBalanceDaoImp(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MoneyBalance.class);
	}


	@Override
	public MoneyBalance createNewOne(String user, Long currency) {

		if (!assertString(user) || currency == null) return null;

		MoneyBalance balance = new MoneyBalance(null, user,
				currency, new BigDecimal(0),
				new Date(System.currentTimeMillis())
		);

		try {
			create(balance);
			return getUserBalance(user, currency);
		} catch (SQLException e) {
			log.throwing(getClass().getName(), "createNewOne", e);
			return null;
		}
	}

	@Override
	public List<MoneyBalance> getUserBalanceList(String user) {

		if (!assertString(user)) return null;

		try {
			return queryForEq(ACCOUNT_ID, user);
		} catch (SQLException e) {
			log.throwing(getClass().getName(), "getUserBalanceList", e);
			return null;
		}
	}


	@Override
	public List<MoneyBalance> getUserBalanceList(String user, Page page) {

		if (!assertString(user) || page == null) return null;

		try {
			return queryBuilder().offset(page.getStartRow())
					.limit(page.getPageSize())
					.orderBy(ID, false)
					.where()
					.eq(ACCOUNT_ID, user)
			.query();
		} catch (SQLException e) {
			log.throwing(getClass().getName(), "getUserBalanceList", e);
			return null;
		}
	}


	@Override
	public MoneyBalance getUserBalance(String user, Long currency) {

		if (!assertString(user) || currency == null) return null;

		try {
			return queryBuilder().where()
					.eq(ACCOUNT_ID, user)
					.and()
					.eq(CURRENCY, currency)
					.queryForFirst();
		} catch (SQLException e) {
			log.throwing(getClass().getName(), "getUserBalance", e);
			return null;
		}
	}

	@Override
	public MoneyBalance getOrCrateUserBalance(String user, Long currency) {

		if (!assertString(user) || currency == null) return null;

		try {
			MoneyBalance b = queryBuilder().where()
					.eq(ACCOUNT_ID, user)
					.and()
					.eq(CURRENCY, currency)
			.queryForFirst();

			return b != null ? b : createNewOne(user, currency);

		} catch (SQLException e) {
			log.throwing(getClass().getName(), "getOrCrateUserBalance", e);
			return null;
		}
	}


	@Override
	public boolean updateBalance(MoneyBalance balance) {

		try {
			return update(balance) == 1;
		} catch (SQLException e) {
			log.throwing(getClass().getName(), "updateBalance", e);
			return false;
		}
	}

}
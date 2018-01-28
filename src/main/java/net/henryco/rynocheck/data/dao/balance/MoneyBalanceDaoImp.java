package net.henryco.rynocheck.data.dao.balance;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import com.j256.ormlite.support.ConnectionSource;
import net.henryco.rynocheck.data.dao.RynoCheckDao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyBalance;
import net.henryco.rynocheck.data.page.Page;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static net.henryco.rynocheck.data.model.MoneyBalance.ACCOUNT_ID;
import static net.henryco.rynocheck.data.model.MoneyBalance.CURRENCY;
import static net.henryco.rynocheck.data.model.MoneyBalance.ID;

@Component @Singleton
public class MoneyBalanceDaoImp extends RynoCheckDao<MoneyBalance, Long> implements MoneyBalanceDao {

	@Inject
	public MoneyBalanceDaoImp(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MoneyBalance.class);
	}


	@Override
	public MoneyBalance createNewOne(String user, Currency currency) {

		if (!assertString(user) || currency == null) return null;

		MoneyBalance balance = new MoneyBalance(null, user,
				currency.getId(), new BigDecimal(0),
				new Date(System.currentTimeMillis())
		);

		try {
			create(balance);
			return getUserBalance(user, currency);
		} catch (SQLException e) {
			return null;
		}
	}

	@Override
	public List<MoneyBalance> getUserBalanceList(String user) {

		if (!assertString(user)) return null;

		try {
			return queryForEq(ACCOUNT_ID, user);
		} catch (SQLException e) {
			e.printStackTrace();
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
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public MoneyBalance getUserBalance(String user, Currency currency) {

		if (!assertString(user) || currency == null) return null;

		try {
			return queryBuilder().where()
					.eq(ACCOUNT_ID, user)
					.and()
					.eq(CURRENCY, currency.getId())
					.queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
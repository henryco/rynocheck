package net.henryco.rynocheck.data.dao.currency;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import com.j256.ormlite.support.ConnectionSource;
import net.henryco.rynocheck.data.dao.RynoCheckDao;
import net.henryco.rynocheck.data.model.Currency;

import java.sql.SQLException;

import static net.henryco.rynocheck.data.model.Currency.CURRENCY_CODE;
import static net.henryco.rynocheck.data.model.Currency.CURRENCY_NAME;

@Component @Singleton
public class CurrencyDaoImp extends RynoCheckDao<Currency, Long>
		implements CurrencyDao {

	@Inject
	public CurrencyDaoImp(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Currency.class);
	}


	@Override
	public Currency getCurrencyByCode(String code) {

		if (!assertString(code)) return null;

		try {
			return queryBuilder().where()
					.eq(CURRENCY_CODE, code.trim())
			.queryForFirst();
		} catch (SQLException e) {
			return null;
		}
	}


	@Override
	public boolean isExists(Currency currency) {

		try {
			return queryBuilder().where()
					.eq(CURRENCY_CODE, currency.getCode())
					.or()
					.eq(CURRENCY_NAME, currency.getName())
			.countOf() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}


	@Override
	public boolean addCurrency(Currency currency) {

		if (isExists(currency)) return false;

		try {
			return create(currency) == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
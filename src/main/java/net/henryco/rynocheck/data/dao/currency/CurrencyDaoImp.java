package net.henryco.rynocheck.data.dao.currency;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import com.j256.ormlite.support.ConnectionSource;
import lombok.val;
import net.henryco.rynocheck.data.dao.RynoCheckDao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.page.Page;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static net.henryco.rynocheck.data.model.Currency.*;

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

		if (currency == null) return true;

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


	/**
	 * Update currency transaction fee.
	 * @param code code of currency
	 * @param author last fee recipient
	 * @param emitter new emitter
	 * @param fee float from 0 to 1
	 * @return true if updated
	 */ @Override
	public boolean updateFeeInfo(String code, String author, String emitter, String fee) {

		if (!assertString(code) || !assertString(emitter)
				|| !assertString(fee) || !assertString(author))
			return false;

		val currency = getCurrencyByCode(code);
		if (currency == null) return false;

		if (!currency.getEmitter().equals(author))
			return false;

		currency.setFee(new BigDecimal(fee));
		currency.setEmitter(emitter);

		try {
			return update(currency) == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	@Override
	public List<Currency> getCurrencies() {

		try {
			return queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public List<Currency> getCurrencies(String emitter) {

	 	if (emitter == null || emitter.trim().isEmpty())
	 		return getCurrencies();

		try {
			return queryForEq(CURRENCY_EMITTER, emitter);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public List<Currency> getCurrencies(Page page) {

	 	if (page == null) return getCurrencies();

	 	try {
	 		return queryBuilder().offset(page.getStartRow())
					.limit(page.getPageSize())
					.orderBy(CURRENCY_ID, false)
			.query();

		} catch (SQLException e) {
	 		e.printStackTrace();
	 		return null;
		}
	}


	@Override
	public List<Currency> getCurrencies(String emitter, Page page) {

	 	if (emitter == null || emitter.trim().isEmpty()) {

	 		if (page == null) return getCurrencies();
	 		else return getCurrencies(page);

	 	} else if (page == null) return getCurrencies(emitter);

	 	try {

	 		return queryBuilder().offset(page.getStartRow())
					.limit(page.getPageSize())
					.orderBy(CURRENCY_ID, false)
					.where()
					.eq(CURRENCY_EMITTER, emitter)
			.query();

		} catch (SQLException e) {
	 		e.printStackTrace();
	 		return null;
		}
	}



}
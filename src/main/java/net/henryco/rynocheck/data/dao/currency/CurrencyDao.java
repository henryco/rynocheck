package net.henryco.rynocheck.data.dao.currency;

import com.j256.ormlite.dao.Dao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.page.DaoPage;

import java.util.List;

public interface CurrencyDao extends Dao<Currency, Long> {

	Currency getCurrencyByCode(String code);

	boolean isExists(Currency currency);

	boolean addCurrency(Currency currency);

	boolean updateFeeInfo(String code, String author, String recipient, String fee);

	List<Currency> getCurrencies();

	List<Currency> getCurrencies(DaoPage page);
}
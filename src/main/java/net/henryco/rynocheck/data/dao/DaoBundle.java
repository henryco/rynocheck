package net.henryco.rynocheck.data.dao;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import lombok.Data;
import net.henryco.rynocheck.data.dao.account.MoneyAccountDao;
import net.henryco.rynocheck.data.dao.currency.CurrencyDao;
import net.henryco.rynocheck.data.dao.session.SessionDao;
import net.henryco.rynocheck.data.dao.transaction.MoneyTransactionDao;
import net.henryco.rynocheck.data.dao.wallet.MoneyBalanceDao;

@Data @Component
public class DaoBundle {

	private final MoneyTransactionDao transactionDao;
	private final MoneyBalanceDao balanceDao;
	private final MoneyAccountDao accountDao;
	private final CurrencyDao currencyDao;
	private final SessionDao sessionDao;

	@Inject
	public DaoBundle(MoneyTransactionDao transactionDao,
					 MoneyBalanceDao balanceDao,
					 MoneyAccountDao accountDao,
					 CurrencyDao currencyDao,
					 SessionDao sessionDao) {
		this.transactionDao = transactionDao;
		this.balanceDao = balanceDao;
		this.accountDao = accountDao;
		this.currencyDao = currencyDao;
		this.sessionDao = sessionDao;
	}
}
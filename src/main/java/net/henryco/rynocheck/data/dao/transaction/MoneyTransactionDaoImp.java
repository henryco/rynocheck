package net.henryco.rynocheck.data.dao.transaction;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import com.j256.ormlite.support.ConnectionSource;
import net.henryco.rynocheck.data.dao.RynoCheckDao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyTransaction;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static net.henryco.rynocheck.data.model.MoneyTransaction.RECEIVER_ID;
import static net.henryco.rynocheck.data.model.MoneyTransaction.SENDER_ID;

@Component @Singleton
public class MoneyTransactionDaoImp extends RynoCheckDao<MoneyTransaction, Long>
		implements MoneyTransactionDao {

	@Inject
	public MoneyTransactionDaoImp(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, MoneyTransaction.class);
	}


	@Override
	public List<MoneyTransaction> getUserTransactions(String user) {
		try {
			return queryBuilder().where()
					.eq(SENDER_ID, user)
					.or()
					.eq(RECEIVER_ID, user)
			.query();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public List<MoneyTransaction> getUserTransactions(String user, Currency currency) {
		return getUserTransactions(user).stream()
				.filter(t -> t.getCurrency().getId().equals(currency.getId()))
		.collect(Collectors.toList());
	}

}
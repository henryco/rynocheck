package net.henryco.rynocheck.data.dao.transaction;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import com.j256.ormlite.support.ConnectionSource;
import lombok.val;
import net.henryco.rynocheck.data.dao.RynoCheckDao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyTransaction;
import net.henryco.rynocheck.data.page.DaoPage;

import java.sql.SQLException;
import java.util.List;

import static net.henryco.rynocheck.data.model.MoneyTransaction.*;

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

		if (!assertString(user)) return null;

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
	public List<MoneyTransaction> getUserTransactions(String user, DaoPage page) {

		if (!assertString(user) || page == null) return null;

		try {
			return queryBuilder().offset(page.getStartRow())
					.limit(page.getPageSize())
					.orderBy(ID, false)
					.where()
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

		if (!assertString(user) || currency == null) return null;

		try {
			val whereCurrency = queryBuilder()
					.where().eq(CURRENCY, currency.getId());

			val whereUser = queryBuilder()
					.where().eq(SENDER_ID, user).or().eq(RECEIVER_ID, user);

			//noinspection unchecked
			return queryBuilder().where().and(whereCurrency, whereUser).query();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public List<MoneyTransaction> getUserTransactions(String user, Currency currency, DaoPage page) {

		if (!assertString(user) || currency == null || page == null) return null;


		try {
			val whereCurrency = queryBuilder()
					.where().eq(CURRENCY, currency.getId());

			val whereUser = queryBuilder()
					.where().eq(SENDER_ID, user).or().eq(RECEIVER_ID, user);

			//noinspection unchecked
			return queryBuilder().offset(page.getStartRow())
					.limit(page.getPageSize())
					.orderBy(ID, false)
					.where()
					.and(whereCurrency, whereUser)
			.query();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public boolean saveTransaction(MoneyTransaction transaction) {

		if (transaction == null) return false;

		try {
			return create(transaction) == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
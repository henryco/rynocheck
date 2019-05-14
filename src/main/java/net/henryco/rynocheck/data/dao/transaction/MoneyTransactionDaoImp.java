package net.henryco.rynocheck.data.dao.transaction;


import com.github.henryco.injector.meta.annotations.Provide;
import com.j256.ormlite.support.ConnectionSource;
import lombok.val;
import net.henryco.rynocheck.data.dao.RynoCheckDao;
import net.henryco.rynocheck.data.model.MoneyTransaction;
import net.henryco.rynocheck.data.page.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.List;

import static net.henryco.rynocheck.data.model.MoneyTransaction.*;

@Provide
@Singleton
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
	public List<MoneyTransaction> getUserTransactions(String user, Page page) {

		if (!assertString(user) || page == null) return null;

		try {
			return queryBuilder().offset(page.getStartRow())
					.limit(page.getPageSize())
					.orderBy(TIME, false)
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
	public List<MoneyTransaction> getUserTransactions(String user, Long currency) {

		if (!assertString(user) || currency == null) return null;

		try {

			val where = queryBuilder().where();

			val whereCurrency = where.eq(CURRENCY, currency);
			val whereUser = where.eq(SENDER_ID, user).or().eq(RECEIVER_ID, user);

			//noinspection unchecked
			return where.and(whereCurrency, whereUser).query();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public List<MoneyTransaction> getUserTransactions(String user, Long currency, Page page) {

		if (!assertString(user) || currency == null || page == null) return null;


		try {
			val where = queryBuilder().offset(page.getStartRow())
					.limit(page.getPageSize())
					.orderBy(TIME, false)
			.where();

			val whereCurrency = where.eq(CURRENCY, currency);
			val whereUser = where.eq(SENDER_ID, user).or().eq(RECEIVER_ID, user);

			//noinspection unchecked
			return where.and(whereCurrency, whereUser)
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
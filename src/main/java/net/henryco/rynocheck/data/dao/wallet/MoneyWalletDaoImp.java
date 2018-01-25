package net.henryco.rynocheck.data.dao.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import com.j256.ormlite.support.ConnectionSource;
import net.henryco.rynocheck.data.dao.RynoCheckDao;
import net.henryco.rynocheck.data.model.entity.MoneyWallet;

import java.sql.SQLException;

@Component @Singleton
public class MoneyWalletDaoImp extends RynoCheckDao<MoneyWallet, Long> implements MoneyWalletDao {

	@Inject
	public MoneyWalletDaoImp(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MoneyWallet.class);
	}

}
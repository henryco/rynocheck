package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import com.github.henryco.injector.meta.annotations.Named;
import com.github.henryco.injector.meta.annotations.Provide;
import com.github.henryco.injector.meta.annotations.Singleton;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.extern.java.Log;
import net.henryco.rynocheck.data.dao.account.MoneyAccountDao;
import net.henryco.rynocheck.data.model.entity.MoneyAccount;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.SQLException;

/**
 * @author Henry on 15/01/18.
 */
@Module(include = {
 		PluginModule.class
}) @Log public class DataModule {


	@Provide @Singleton
	public ConnectionSource connectionSource(@Named("dbName") String dbName) throws SQLException {
		return new JdbcPooledConnectionSource("jdbc:sqlite:" + dbName);
	}


	@Provide("dbName") @Singleton
	public String provideDataBaseName(Plugin plugin) {

		String dbDir = plugin.getDataFolder().getAbsolutePath();
		if (new File(dbDir).mkdirs())
			log.info("Created database dir: " + dbDir);
		return dbDir + File.separator + plugin.getConfig().getString(
				"database.sqlite.name", "rynocheck.db"
		);
	}


	@Provide @Singleton
	public MoneyAccountDao moneyAccountDao(ConnectionSource source) throws SQLException {
		TableUtils.createTableIfNotExists(source, MoneyAccount.class);
		return new MoneyAccountDao(source);
	}

}
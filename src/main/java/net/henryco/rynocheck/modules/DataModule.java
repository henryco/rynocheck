package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import com.github.henryco.injector.meta.annotations.Named;
import com.github.henryco.injector.meta.annotations.Provide;
import com.github.henryco.injector.meta.annotations.Singleton;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.extern.java.Log;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	public ExecutorService executorService(Plugin plugin) {
		int pool = plugin.getConfig().getInt("transaction.pool.size", 1);
		return Executors.newFixedThreadPool(pool);
	}

}
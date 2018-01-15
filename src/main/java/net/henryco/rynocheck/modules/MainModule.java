package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Module;
import com.github.henryco.injector.meta.annotations.Provide;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.RynoCheckPlugin;
import net.henryco.rynocheck.data.service.IDBConnectionService;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * @author Henry on 14/01/18.
 */
@Module(include = {
		CommandModule.class,
		PluginModule.class
}, targets = {
		RynoCheckPlugin.class,
}) public final class MainModule {


	@Provide @Singleton
	public IDBConnectionService connectionService(@Inject("dbName") String dbName) {
		System.out.println("connectionService(" + dbName +")");
		return IDBConnectionService.Factory.sqlite(dbName);
	}


	@Provide("dbName") @Singleton
	public String provideDataBaseName(Plugin plugin) {
		return plugin.getDataFolder().getAbsolutePath()
				+ File.separator + plugin.getConfig().getString(
				"database.sqlite.name", "rynocheck.db"
		);
	}

}
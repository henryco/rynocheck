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
		CommandModule.class
}, targets = {
		RynoCheckPlugin.class
}) public final class MainModule {


	@Provide @Singleton
	public IDBConnectionService connectionService(@Inject("dbName") String dbName) {
		System.out.println("connectionService(" + dbName +")");
		return IDBConnectionService.Factory.sqlite(dbName);
	}


	@Provide("dbName") @Singleton
	public String provideDataBaseName(Plugin plugin) {
		System.out.println("provideDataBaseName("+plugin+")");
		String absolutePath = plugin.getDataFolder().getAbsolutePath();
		System.out.println("DataPath: "+absolutePath);
		return absolutePath + File.separator + plugin.getConfig().getString(
				"database.sqlite.name", "rynocheck.db"
		);
	}

}
package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import com.github.henryco.injector.meta.annotations.Provide;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.RynoCheckPlugin;
import net.henryco.rynocheck.data.dao.currency.CurrencyDao;
import net.henryco.rynocheck.data.model.Currency;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * @author Henry on 14/01/18.
 */
@Module(include = {
		PluginModule.class,
		DataModule.class
}, targetsRootClass = {
		RynoCheckPlugin.class
}, componentsRootClass = {
		RynoCheckPlugin.class
}, targets = {
		RynoCheckPlugin.class
}) public final class RootModule {


	@Provide("currency_config") @Singleton
	public FileConfiguration currencyConfig(CurrencyDao dao, Plugin plugin) {

		plugin.getLogger().info("Configuring default currencies");

		val fee = plugin.getConfig().getString("currency.default.fee.value");
		val rec = plugin.getConfig().getString("currency.default.fee.recipient");
		val list = plugin.getConfig().getStringList("currency.default.entity");
		val NAME = list.get(0);
		val CODE = list.get(1);
		val LIM = list.get(2);

		val currency = new Currency(null, NAME, CODE, new BigDecimal(LIM), new BigDecimal(fee), rec);
		if (!dao.isExists(currency)) {
			try {
				dao.create(currency);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return plugin.getConfig();
	}


	@Provide("TransactionPoolSize")
	public int transactionPoolSize(FileConfiguration configuration) {
		return configuration.getInt("transaction.pool.size", 1);
	}

	@Provide("selfEnable")
	public boolean transactionSelf(FileConfiguration configuration) {
		return configuration.getBoolean("transaction.self", false);
	}
}
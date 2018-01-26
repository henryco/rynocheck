package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import com.github.henryco.injector.meta.annotations.Provide;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.RynoCheckPlugin;
import net.henryco.rynocheck.data.dao.currency.CurrencyDao;
import net.henryco.rynocheck.data.model.Currency;
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
	public int currencyConfig(CurrencyDao dao, Plugin plugin) {

		plugin.getLogger().info("Configuring default currencies");

		val list = plugin.getConfig().getStringList("currency.default");
		val NAME = list.get(0);
		val CODE = list.get(1);
		val LIM = list.get(2);

		val currency = new Currency(null, NAME, CODE, new BigDecimal(LIM));
		if (!dao.isExists(currency)) {
			try {
				dao.create(currency);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

}
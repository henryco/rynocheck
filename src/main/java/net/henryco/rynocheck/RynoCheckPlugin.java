package net.henryco.rynocheck;

import com.github.henryco.injector.GrInjector;
import com.github.henryco.injector.meta.annotations.Inject;
import net.henryco.rynocheck.command.DecisionCommandExecutor;
import net.henryco.rynocheck.command.NapCommandExecutor;
import net.henryco.rynocheck.command.wallet.WalletCommandExecutor;
import net.henryco.rynocheck.modules.RootModule;
import net.henryco.rynocheck.modules.PluginModule;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Henry on 11/01/18.
 */
public class RynoCheckPlugin extends JavaPlugin {


	private @Inject DecisionCommandExecutor decisionCommandExecutor;
	private @Inject WalletCommandExecutor walletCommandExecutor;
	private @Inject NapCommandExecutor napCommandExecutor;


	@Override
	public void onEnable() {
		super.onEnable();

		PluginModule.setStatic_plugin(this);
		GrInjector.addModules(RootModule.class);
		GrInjector.inject(RynoCheckPlugin.this);

		getLogger().info("Wellcome to CYBER SOMALIA");
		getCommand("nap").setExecutor(napCommandExecutor);
		getCommand("wallet").setExecutor(walletCommandExecutor);
		getCommand("y").setExecutor(decisionCommandExecutor);
		getCommand("n").setExecutor(decisionCommandExecutor);
	}


	@Override
	public void onDisable() {
		super.onDisable();
		getLogger().info("U DON'T KNOW THE WAE");
	}

}
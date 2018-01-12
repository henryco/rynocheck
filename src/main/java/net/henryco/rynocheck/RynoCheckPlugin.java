package net.henryco.rynocheck;

import net.henryco.rynocheck.command.NapCommandExecutor;
import net.henryco.rynocheck.command.WalletCommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Henry on 11/01/18.
 */
public class RynoCheckPlugin extends JavaPlugin {


	@Override
	public void onEnable() {
		super.onEnable();
		getLogger().info("Wellcome to CYBER SOMALIA");
		getCommand("nap").setExecutor(new NapCommandExecutor(this));
		getCommand("wallet").setExecutor(new WalletCommandExecutor(this));
	}


	@Override
	public void onDisable() {
		super.onDisable();
		getLogger().info("NA ZMEYU NIE NASTUPAI");
	}

}
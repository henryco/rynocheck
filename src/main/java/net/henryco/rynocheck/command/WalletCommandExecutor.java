package net.henryco.rynocheck.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 12/01/18.
 */
public class WalletCommandExecutor extends RynoCheckExecutor {


	public WalletCommandExecutor(Plugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
	}
}
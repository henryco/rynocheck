package net.henryco.rynocheck.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 12/01/18.
 */
public abstract class RynoCheckExecutor implements CommandExecutor {

	private final Plugin plugin;

	public RynoCheckExecutor(Plugin plugin) {
		this.plugin = plugin;
	}

	protected Plugin getPlugin() {
		return plugin;
	}

}
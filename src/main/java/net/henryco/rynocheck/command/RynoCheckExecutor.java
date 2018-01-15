package net.henryco.rynocheck.command;

import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 12/01/18.
 */
public abstract class RynoCheckExecutor implements CommandExecutor {

	private final CommandContext commandContext;
	private final Plugin plugin;

	public RynoCheckExecutor(CommandContext commandContext,
							 Plugin plugin) {
		this.commandContext = commandContext;
		this.plugin = plugin;
		System.out.println("RynoCheckExecutor(" + commandContext + ", " + plugin + ")");
	}

	protected Plugin getPlugin() {
		return plugin;
	}

	protected CommandContext getContext() {
		return commandContext;
	}

}
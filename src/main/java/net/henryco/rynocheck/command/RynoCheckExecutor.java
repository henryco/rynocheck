package net.henryco.rynocheck.command;

import lombok.extern.java.Log;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 12/01/18.
 */ @Log
public abstract class RynoCheckExecutor implements CommandExecutor {

	private final CommandContext commandContext;
	private final Plugin plugin;

	public RynoCheckExecutor(CommandContext commandContext,
							 Plugin plugin, String ... commandNames) {

		this.commandContext = commandContext;
		this.plugin = plugin;
		register(commandNames);
	}


	private void register(String[] commandNames) {
		if (commandNames == null) return;
		for (String commandName : commandNames)
			plugin.getServer().getPluginCommand(commandName).setExecutor(this);
	}

	protected Plugin getPlugin() {
		return plugin;
	}

	@SuppressWarnings("WeakerAccess")
	protected CommandContext getContext() {
		return commandContext;
	}

	@SuppressWarnings("WeakerAccess")
	protected void onException(Exception exception) {
		exception.printStackTrace();
		log.throwing(getClass().getName(), "onCommand", exception);
	}


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		try {
			return onCommandExecute(sender, command, label, args);
		} catch (Exception e) {
			onException(e);
			return false;
		}
	}

	protected abstract boolean onCommandExecute(CommandSender sender, Command command,
												String label, String[] args) throws Exception;
}

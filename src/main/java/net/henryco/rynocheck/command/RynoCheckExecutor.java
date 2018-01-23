package net.henryco.rynocheck.command;

import lombok.Getter;
import net.henryco.rynocheck.RynoCheck;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 12/01/18.
 */
public abstract class RynoCheckExecutor implements CommandExecutor, RynoCheck {

	private final @Getter CommandContext context;
	private final @Getter Plugin plugin;

	public RynoCheckExecutor(CommandContext context,
							 Plugin plugin, String ... commandNames) {
		this.context = context;
		this.plugin = plugin;
		register(commandNames);
	}


	private void register(String[] commandNames) {

		if (commandNames == null) return;
		for (String commandName : commandNames) {

			PluginCommand command = plugin.getServer().getPluginCommand(commandName);
			if (command == null)
				throw new RuntimeException("There is no command: " + commandName);
			command.setExecutor(this);
		}
	}


	@SuppressWarnings("WeakerAccess")
	protected void onException(Exception exception) {
		exception.printStackTrace();
		getLogger().throwing(getClass().getName(), "onCommand", exception);
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

package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 12/01/18.
 */ @Component @Singleton
public class WalletCommandExecutor extends RynoCheckExecutor {


	@Inject
	public WalletCommandExecutor(CommandContext commandContext,
								 Plugin plugin) {
		super(commandContext, plugin, "wallet");
	}

	@Override
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		return false;
	}
}
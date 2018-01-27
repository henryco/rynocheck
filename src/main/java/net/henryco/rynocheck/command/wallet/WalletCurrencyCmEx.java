package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

@Component @Singleton
public class WalletCurrencyCmEx extends RynoCheckExecutor {


	@Inject
	public WalletCurrencyCmEx(CommandContext context, Plugin plugin) {
		super(context, plugin, "wallet-currency");
	}

	@Override
	protected boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {
		return false;
	}

}
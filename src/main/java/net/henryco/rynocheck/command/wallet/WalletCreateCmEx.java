package net.henryco.rynocheck.command.wallet;


import com.github.henryco.injector.meta.annotations.Provide;
import lombok.extern.java.Log;
import net.henryco.rynocheck.account.MoneyAccountException;
import net.henryco.rynocheck.account.MoneyAccountService;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Henry on 15/01/18.
 */
@Provide @Log @Singleton
public class WalletCreateCmEx extends RynoCheckExecutor {

	private final MoneyAccountService moneyAccountService;

	@Inject
	public WalletCreateCmEx(
			MoneyAccountService moneyAccountService,
			CommandContext commandContext,
			Plugin plugin
	) {
		super(commandContext, plugin, 3, "wallet-create");
		this.moneyAccountService = moneyAccountService;
	}

	@Override // <wallet-create> {name} {password} {optional email}
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) {

		if (args[0] == null) return false;
		if (!(sender instanceof Player)) {
			return true; // todo
		}

		try {
			moneyAccountService.addAccount(args[0], args[1], args[2]);
			sender.sendMessage("Done, account: " + args[0] + " created, now you can login.");
			return true;
		} catch (MoneyAccountException e) {
			sender.sendMessage(e.getMessage());
			return true;
		}
	}

}

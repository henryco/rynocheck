package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Named;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.command.wallet.sub.WalletSubCommand;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 12/01/18.
 */ @Component @Singleton
public class WalletCommandExecutor extends RynoCheckExecutor {

 	private static final String SEND = "send";
	private static final String BALANCE = "balance";
	private static final String HISTORY = "history";

	private final WalletSubCommand send;
	private final WalletSubCommand history;
	private final WalletSubCommand balance;


	@Inject
	public WalletCommandExecutor(@Named("SCSend") WalletSubCommand send,
								 @Named("SCHist") WalletSubCommand history,
								 @Named("SCBlns") WalletSubCommand balance,
								 CommandContext commandContext,
								 Plugin plugin) {

		super(commandContext, plugin, "wallet");
		this.balance = balance;
		this.history = history;
		this.send = send;
	}

	@Override
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		if (args.length == 0) return false;
		if (!(sender instanceof Player)) {
			// todo
			return true;
		}

		switch (args[0]) {
			case SEND: return send.executeSubCommand(sender, args);
			case BALANCE: return balance.executeSubCommand(sender, args);
			case HISTORY: return history.executeSubCommand(sender, args);
		}

		return false;
	}




}
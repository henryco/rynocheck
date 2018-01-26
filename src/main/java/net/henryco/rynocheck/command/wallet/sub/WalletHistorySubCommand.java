package net.henryco.rynocheck.command.wallet.sub;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Singleton;
import org.bukkit.command.CommandSender;

@Component("SCHist") @Singleton
public class WalletHistorySubCommand implements WalletSubCommand {

	@Override
	public boolean executeSubCommand(CommandSender sender, String[] args) {
		return false;
	}
}
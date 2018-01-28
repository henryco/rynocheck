package net.henryco.rynocheck.command.wallet.sub;

import org.bukkit.command.CommandSender;

public interface WalletSubCommand {

	int PAGE_SIZE = 9;
	String ALL = "*";

	boolean executeSubCommand(CommandSender sender, String[] args);
}
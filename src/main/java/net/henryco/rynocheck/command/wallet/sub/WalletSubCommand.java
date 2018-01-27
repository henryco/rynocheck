package net.henryco.rynocheck.command.wallet.sub;

import org.bukkit.command.CommandSender;

public interface WalletSubCommand {

	String ALL = "*";

	boolean executeSubCommand(CommandSender sender, String[] args);
}
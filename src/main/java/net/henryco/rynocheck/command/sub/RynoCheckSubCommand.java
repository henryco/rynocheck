package net.henryco.rynocheck.command.sub;

import org.bukkit.command.CommandSender;

public interface RynoCheckSubCommand {

	int PAGE_SIZE = 9;
	String ALL = "*";

	boolean executeSubCommand(CommandSender sender, String[] args);
}
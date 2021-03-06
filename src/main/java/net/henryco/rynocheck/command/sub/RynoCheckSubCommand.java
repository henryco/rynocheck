package net.henryco.rynocheck.command.sub;

import org.bukkit.command.CommandSender;

public interface RynoCheckSubCommand {

	int PAGE_SIZE = 9;
	String EMPTY_MESSAGE = "+EMPTY+";
	String ALL = "*";

	boolean executeSubCommand(CommandSender sender, String[] args);

	default boolean caseSensitive() {
		return false;
	}

	default boolean strict() {
		return false;
	}

	int maxNumberOfArgs();

	String name();
}
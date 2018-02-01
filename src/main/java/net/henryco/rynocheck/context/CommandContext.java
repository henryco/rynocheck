package net.henryco.rynocheck.context;

import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.function.Function;

/**
 * @author Henry on 14/01/18.
 */
public interface CommandContext {

	String YN_OPTION = " /< y | N >";

	default void showDecisionQuestion(CommandSender sender) {
		sender.sendMessage("Are you want to continue?" + YN_OPTION);
	}

	CommandContext addPositive(UUID uuid, Function<Void, ?> function);

	CommandContext addNegative(UUID uuid, Function<Void, ?> function);

	<T> T positive(UUID uuid);

	<T> T negative(UUID uuid);

	void wipe(UUID uuid);
}

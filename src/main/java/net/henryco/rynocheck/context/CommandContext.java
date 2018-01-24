package net.henryco.rynocheck.context;

import java.util.UUID;
import java.util.function.Function;

/**
 * @author Henry on 14/01/18.
 */
public interface CommandContext {

	CommandContext addPositive(UUID uuid, Function<Void, ?> function);

	CommandContext addNegative(UUID uuid, Function<Void, ?> function);

	<T> T positive(UUID uuid);

	<T> T negative(UUID uuid);

	void wipe(UUID uuid);
}

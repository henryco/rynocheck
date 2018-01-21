package net.henryco.rynocheck.context;

import java.util.UUID;
import java.util.function.Function;

/**
 * @author Henry on 14/01/18.
 */
public interface CommandContext {

	void add(UUID uuid, Function<Void, ?> function);

	<T> T release(UUID uuid);

	void wipe(UUID uuid);

}

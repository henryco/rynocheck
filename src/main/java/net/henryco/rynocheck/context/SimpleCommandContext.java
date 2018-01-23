package net.henryco.rynocheck.context;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author Henry on 14/01/18.
 */
@Component @Singleton
public class SimpleCommandContext implements CommandContext {

	private final Map<UUID, Function<Void, ?>> functionMap = new HashMap<>();

	public SimpleCommandContext() { }

	@Override
	public void add(UUID uuid, Function<Void, ?> function) {
		wipe(uuid);
		functionMap.put(uuid, function);
	}

	@Override @SuppressWarnings("unchecked")
	public <T> T release(UUID uuid) {
		Function<Void, ?> function = functionMap.remove(uuid);
		if (function == null) return null;
		return (T) function.apply(null);
	}

	@Override
	public void wipe(UUID uuid) {
		functionMap.remove(uuid);
	}
}
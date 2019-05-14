package net.henryco.rynocheck.context;


import com.github.henryco.injector.meta.annotations.Provide;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author Henry on 14/01/18.
 */ @Provide @Singleton
public class SimpleCommandContext implements CommandContext {

	private final Map<UUID, Function<Void, ?>> positiveMap = new HashMap<>();
	private final Map<UUID, Function<Void, ?>> negativeMap = new HashMap<>();

	public SimpleCommandContext() { }

	@Override
	public CommandContext addPositive(UUID uuid, Function<Void, ?> function) {
		positiveMap.put(uuid, function);
		return this;
	}

	@Override
	public CommandContext addNegative(UUID uuid, Function<Void, ?> function) {
		negativeMap.put(uuid, function);
		return this;
	}

	@Override
	public <T> T positive(UUID uuid) {
		negativeMap.remove(uuid);
		return release(uuid, positiveMap);
	}

	@Override
	public <T> T negative(UUID uuid) {
		positiveMap.remove(uuid);
		return release(uuid, negativeMap);
	}

	@Override
	public void wipe(UUID uuid) {
		positiveMap.remove(uuid);
		negativeMap.remove(uuid);
	}

	@SuppressWarnings("unchecked")
	private static <T> T release(UUID uuid, Map<UUID, Function<Void, ?>> map) {
		Function<Void, ?> function = map.remove(uuid);
		if (function == null) return null;
		return (T) function.apply(null);
	}

}
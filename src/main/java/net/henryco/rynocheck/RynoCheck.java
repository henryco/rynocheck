package net.henryco.rynocheck;

import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public interface RynoCheck {

	Logger LOGGER = Logger.getLogger("RynoCheck");

	Plugin getPlugin();

	default Logger getLogger() {
		return getPlugin() == null ? LOGGER : getPlugin().getLogger();
	}
}
package net.henryco.rynocheck.event;

import lombok.Getter;
import net.henryco.rynocheck.RynoCheck;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class RynoCheckEventListener implements Listener, RynoCheck {

	private @Getter final Plugin plugin;

	public RynoCheckEventListener(Plugin plugin) {
		this.plugin = plugin;
		register();
	}

	private void register() {
		getPlugin().getServer()
				.getPluginManager()
				.registerEvents(this, plugin);
	}

}
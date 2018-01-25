package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import com.github.henryco.injector.meta.annotations.Provide;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.Setter;
import lombok.val;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * @author Henry on 15/01/18.
 */ @Module
public final class PluginModule {

	private @Setter static Plugin static_plugin;

	@Provide @Singleton
	public Plugin plugin() {

		val plugin = static_plugin;

		if (!plugin.getDataFolder().exists()) {
			if (plugin.getDataFolder().mkdirs()) {
				plugin.getLogger().info("Data folder created");
			}
		}

		val config = new File(plugin.getDataFolder(), "config.yml");
		if (!config.exists()) {
			plugin.getLogger().warning("Cannot find config.yml");
			plugin.saveDefaultConfig();
		}

		return plugin;
	}


}
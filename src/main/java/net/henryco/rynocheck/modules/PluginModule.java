package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import com.github.henryco.injector.meta.annotations.Provide;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.Setter;
import net.henryco.rynocheck.context.SimpleCommandContext;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 15/01/18.
 */@Module(components = {
		SimpleCommandContext.class
}) public final class PluginModule {

	private @Setter static Plugin static_plugin;

	@Provide @Singleton
	public Plugin plugin() {
		return static_plugin;
	}
}
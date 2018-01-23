package net.henryco.rynocheck;

import com.github.henryco.injector.GrInjector;
import com.j256.ormlite.support.ConnectionSource;
import lombok.val;
import net.henryco.rynocheck.modules.PluginModule;
import net.henryco.rynocheck.modules.RootModule;
import net.henryco.rynocheck.utils.PluginClassFinder;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

import static com.github.henryco.injector.GrInjector.getComponent;

/**
 * @author Henry on 11/01/18.
 */
public class RynoCheckPlugin extends JavaPlugin implements Listener {


	@Override
	public void onEnable() {
		super.onEnable();

		val classFinder = new PluginClassFinder(getClassLoader());

		PluginModule.setStatic_plugin(this);
		GrInjector.addModules(classFinder, RootModule.class);
		GrInjector.inject(RynoCheckPlugin.this);

		getLogger().info("Welcome to CYBER SOMALIA");
	}


	@Override
	public void onDisable() {
		super.onDisable();
		try {
			getComponent(ConnectionSource.class).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
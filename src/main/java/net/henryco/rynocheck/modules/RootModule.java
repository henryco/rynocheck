package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import net.henryco.rynocheck.RynoCheckPlugin;

/**
 * @author Henry on 14/01/18.
 */
@Module(include = {
		CommandModule.class,
		PluginModule.class,
		DataModule.class
}, targets = {
		RynoCheckPlugin.class,
}) public final class RootModule {


}
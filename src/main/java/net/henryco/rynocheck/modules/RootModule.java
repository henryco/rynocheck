package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import net.henryco.rynocheck.RynoCheckPlugin;

/**
 * @author Henry on 14/01/18.
 */
@Module(include = {
		PluginModule.class,
		DataModule.class
}, targetsRootClass = {
		RynoCheckPlugin.class
}, componentsRootClass = {
		RynoCheckPlugin.class
}, targets = {
		RynoCheckPlugin.class
}) public final class RootModule {


}
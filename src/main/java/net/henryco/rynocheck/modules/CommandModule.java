package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import net.henryco.rynocheck.command.DecisionCommandExecutor;
import net.henryco.rynocheck.command.NapCommandExecutor;
import net.henryco.rynocheck.command.WalletCommandExecutor;

/**
 * @author Henry on 15/01/18.
 */
@Module(components = {
		DecisionCommandExecutor.class,
		NapCommandExecutor.class,
		WalletCommandExecutor.class,
}, include = {
		PluginModule.class,
		ContextModule.class
}) public final class CommandModule {

}
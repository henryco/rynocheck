package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import net.henryco.rynocheck.command.DecisionCommandExecutor;
import net.henryco.rynocheck.command.NapCommandExecutor;
import net.henryco.rynocheck.command.wallet.WalletCommandExecutor;
import net.henryco.rynocheck.command.wallet.WalletCreateCmEx;
import net.henryco.rynocheck.command.wallet.WalletLoginCmEx;
import net.henryco.rynocheck.command.wallet.WalletLogoutCmEx;

/**
 * @author Henry on 15/01/18.
 */
@Module(components = {
		DecisionCommandExecutor.class,
		NapCommandExecutor.class
}, include = {
		PluginModule.class,
		WalletModule.class
}) public final class CommandModule {

}
package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import net.henryco.rynocheck.command.wallet.WalletCommandExecutor;
import net.henryco.rynocheck.command.wallet.WalletCreateCmEx;
import net.henryco.rynocheck.command.wallet.WalletLoginCmEx;
import net.henryco.rynocheck.command.wallet.WalletLogoutCmEx;

/**
 * @author Henry on 15/01/18.
 */
@Module(include = {
		DataModule.class,
		PluginModule.class
}, components = {
		WalletCommandExecutor.class,
		WalletCreateCmEx.class,
		WalletLoginCmEx.class,
		WalletLogoutCmEx.class
}) public class WalletModule {

}
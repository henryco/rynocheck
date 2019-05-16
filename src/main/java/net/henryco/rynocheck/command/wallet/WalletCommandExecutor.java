package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Provide;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.command.sub.RynoCheckSubExecutor;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Henry on 12/01/18.
 */ @Provide @Singleton
public class WalletCommandExecutor extends RynoCheckSubExecutor {

	private final RynoCheckSubCommand[] subCommands;

	@Inject
	public WalletCommandExecutor(
			@Named("SCBlns") RynoCheckSubCommand balance,
			@Named("SCHist") RynoCheckSubCommand history,
			@Named("SCSend") RynoCheckSubCommand send,
			CommandContext commandContext,
			Plugin plugin
	) {
		super(commandContext, plugin, "wallet");
		this.subCommands = new RynoCheckSubCommand[] {
				balance, history, send
		};
	}

	@Override
	protected RynoCheckSubCommand[] provideSubCommands() {
		return subCommands;
	}

}
package net.henryco.rynocheck.command.wallet;


import com.github.henryco.injector.meta.annotations.Provide;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.command.sub.RynoCheckSubExecutor;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Provide @Singleton
public class WalletCurrencyCmEx extends RynoCheckSubExecutor {

	private final RynoCheckSubCommand[] subCommands;

	@Inject
	public WalletCurrencyCmEx(
			@Named("SCListCurr") RynoCheckSubCommand list,
			@Named("SCEmitCurr") RynoCheckSubCommand emit,
			@Named("SCAddCurr") RynoCheckSubCommand add,
			@Named("SCSetCurr") RynoCheckSubCommand set,
			CommandContext context,
			Plugin plugin
	) {
		super(context, plugin,"wallet-currency");
		this.subCommands = new RynoCheckSubCommand[] {
				list, emit, add, set
		};
	}

	@Override
	protected RynoCheckSubCommand[] provideSubCommands() {
		return subCommands;
	}
}
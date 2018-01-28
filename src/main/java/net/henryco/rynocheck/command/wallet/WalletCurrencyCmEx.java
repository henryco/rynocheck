package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Named;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Component @Singleton
public class WalletCurrencyCmEx extends RynoCheckExecutor {

	private static final String LIST = "list";
	private static final String EMIT = "emit";
	private static final String ADD = "add";
	private static final String SET = "set";

	private final RynoCheckSubCommand list;
	private final RynoCheckSubCommand emit;
	private final RynoCheckSubCommand add;
	private final RynoCheckSubCommand set;


	@Inject
	public WalletCurrencyCmEx(@Named("SCListCurr") RynoCheckSubCommand list,
							  @Named("SCEmitCurr") RynoCheckSubCommand emit,
							  @Named("SCAddCurr") RynoCheckSubCommand add,
							  @Named("SCSetCurr") RynoCheckSubCommand set,
							  CommandContext context,
							  Plugin plugin) {
		super(context, plugin, "wallet-currency");

		this.list = list;
		this.emit = emit;
		this.add = add;
		this.set = set;
	}

	@Override
	protected boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		if (args.length == 0) return false;
		if (!(sender instanceof Player)) {
			return true; // TODO
		}

		switch (args[0]) {
			case LIST: return list.executeSubCommand(sender, args);
			case EMIT: return emit.executeSubCommand(sender, args);
			case ADD: return add.executeSubCommand(sender, args);
			case SET: return set.executeSubCommand(sender, args);
		}

		return false;
	}

}
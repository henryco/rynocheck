package net.henryco.rynocheck.command;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.service.IDBConnectionService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 12/01/18.
 */ @Component @Singleton
public class WalletCommandExecutor extends RynoCheckExecutor {

	private final IDBConnectionService dbConnectionService;

	@Inject
	public WalletCommandExecutor(IDBConnectionService dbConnectionService,
								 CommandContext commandContext,
								 Plugin plugin) {
		super(commandContext, plugin);
		this.dbConnectionService = dbConnectionService;
		System.out.println("WalletCommandExecutor()");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		return false;
	}
}
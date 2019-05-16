package net.henryco.rynocheck.command;


import com.github.henryco.injector.meta.annotations.Provide;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

/**
 * @author Henry on 14/01/18.
 */ @Provide @Singleton
public class DecisionCommandExecutor extends RynoCheckExecutor {

	private static final String YES = "y";
	private static final String NO = "n";

	@Inject
	public DecisionCommandExecutor(CommandContext commandContext,
								   Plugin plugin) {
		super(commandContext, plugin, 0, YES, NO);
	}

	@Override
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		if (!(sender instanceof Player)) {
			return true;
		}

		UUID uniqueId = ((Player)sender).getUniqueId();
		String name = command.getName();

		if (name.equalsIgnoreCase(YES)) {
			getContext().positive(uniqueId);
			return true;
		}

		if (name.equalsIgnoreCase(NO)) {
			getContext().negative(uniqueId);
			return true;
		}

		return false;
	}

}
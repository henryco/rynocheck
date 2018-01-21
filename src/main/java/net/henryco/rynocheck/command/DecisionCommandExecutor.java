package net.henryco.rynocheck.command;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * @author Henry on 14/01/18.
 */ @Component @Singleton
public class DecisionCommandExecutor extends RynoCheckExecutor {

	private static final String YES = "y";
	private static final String NO = "n";

	@Inject
	public DecisionCommandExecutor(CommandContext commandContext,
								   Plugin plugin) {
		super(commandContext, plugin);
	}

	@Override
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		if (sender instanceof Player) {
			UUID uniqueId = ((Player)sender).getUniqueId();
			String name = command.getName();

			getPlugin().getLogger().info("Player: " + sender);
			getPlugin().getLogger().info("uniqueID: " + uniqueId);
			if (name.equalsIgnoreCase(YES)) {
				getContext().release(uniqueId);
				return true;
			} else if (name.equalsIgnoreCase(NO)) {
				getContext().wipe(uniqueId);
				return true;
			}
		}

		return false;
	}

}
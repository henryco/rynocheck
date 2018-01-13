package net.henryco.rynocheck.command;

import com.github.henryco.injector.GrInjector;
import net.henryco.rynocheck.permission.RynoCheckPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 11/01/18.
 */
public class NapCommandExecutor extends RynoCheckExecutor {

	private static final String NAME = "nap";
	private static final String YES = "agree";
	private static final String NO = "disagree";

	public NapCommandExecutor(Plugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equalsIgnoreCase(NAME) && sender instanceof Player) {

			PermissionAttachment pa = sender.addAttachment(getPlugin());
			switch (args[0]) {

				case YES: {
					sender.sendMessage("Now you are NAP member");
					pa.setPermission(RynoCheckPermissions.NAP, true);
					return true;
				}

				case NO: {
					sender.sendMessage("It's your decision");
					pa.unsetPermission(RynoCheckPermissions.NAP);
					return true;
				}
			}
		}
		return false;
	}
}

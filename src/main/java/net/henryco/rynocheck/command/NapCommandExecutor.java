package net.henryco.rynocheck.command;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.permission.RynoCheckPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import static net.henryco.rynocheck.permission.RynoCheckPermissions.NAP;

/**
 * @author Henry on 11/01/18.
 */ @Component @Singleton
public class NapCommandExecutor extends RynoCheckExecutor {

	private static final String YES = "on";
	private static final String NO = "off";

	@Inject
	public NapCommandExecutor(CommandContext commandContext,
							  Plugin plugin) {
		super(commandContext, plugin, "nap");
	}

	@Override
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		if (sender instanceof Player) {

			final PermissionAttachment pa = sender.addAttachment(getPlugin());

			if (args.length == 0) return false;

			if (YES.equalsIgnoreCase(args[0])) {
				sender.sendMessage("Now you are NAP member");
				pa.setPermission(NAP, true);
				return true;
			} else if (NO.equalsIgnoreCase(args[0])) {

				if (!pa.getPermissible().hasPermission(NAP)) {
					sender.sendMessage("You are not NAP member");
					return true;
				}
				sender.sendMessage("Are you sure? /(y : N)");
				getContext().addPositive(((Player) sender).getUniqueId(), aVoid -> {

					RynoCheckPermissions.Utils.unsetPermission(sender, NAP);
					sender.sendMessage("It's your decision, cowboy");

					NapCommandExecutor.this.getPlugin().getServer().getOnlinePlayers().forEach(p -> getPlugin().getServer()
							.broadcast(((Player) sender).getDisplayName() + " has left the NAP", NAP));

					return true;
				});

				return true;
			}
		}
		return false;
	}
}

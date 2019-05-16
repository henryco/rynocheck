package net.henryco.rynocheck.command.sub;

import lombok.val;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class RynoCheckSubExecutor extends RynoCheckExecutor {


	public RynoCheckSubExecutor(CommandContext context, Plugin plugin, String... commands) {
		this(context, plugin, 100, commands);
	}

	public RynoCheckSubExecutor(CommandContext context, Plugin plugin, int argsNumber, String... commands) {
		super(context, plugin, argsNumber, commands);
	}


	protected abstract RynoCheckSubCommand[] provideSubCommands();


	@Override
	protected boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args)  {

		if (args[0] == null) return false;
		if (!(sender instanceof Player)) {
			return true; // todo
		}

		for (val sub: provideSubCommands()) {
			if (compare(sub.caseSensitive(), sub.name(), args[0])) {
				val arguments = trimArgs(sub.maxNumberOfArgs(), args);

				if (sub.strict()) {
					for (val a: arguments) {
						if (a == null) {
							return false;
						}
					}
				}

				return sub.executeSubCommand(sender, arguments);
			}
		}

		return false;
	}


	private static boolean compare(boolean caseSensitive, String o1, String o2) {
		return caseSensitive ? o1.equals(o2) : o1.equalsIgnoreCase(o2);
	}

	private static String[] trimArgs(int number, String ... args) {

		val lim = Math.min(number, args.length);
		val arr = new String[number];

		//  0  1  2  3  4
		// [a, 3, 4, 5, 6]: 5
		//    [3, 4, 5, 6]: 4 -> lim

		// [b, 3, 4, 5, 6]: 5
		//    [3, 4, 5]   : 3 -> lim

		// [c, 2, 4]      : 3 -> lim
		//    [2, 4, -, -]: 4

		if (lim - 1 >= 0)
			System.arraycopy(args, 1, arr, 0, lim - 1);

		return arr;
	}
}

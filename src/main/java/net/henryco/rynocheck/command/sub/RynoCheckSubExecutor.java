package net.henryco.rynocheck.command.sub;

import lombok.extern.java.Log;
import lombok.val;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

@Log
public abstract class RynoCheckSubExecutor extends RynoCheckExecutor {


	public RynoCheckSubExecutor(CommandContext context, Plugin plugin, String... commands) {
		this(context, plugin, 10, commands);
	}

	public RynoCheckSubExecutor(CommandContext context, Plugin plugin, int argsNumber, String... commands) {
		super(context, plugin, argsNumber, commands);
	}


	protected abstract RynoCheckSubCommand[] provideSubCommands();


	@Override
	protected boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args)  {

		log.info(this.getClass().getSimpleName() + ": onCommandExecute: " + Arrays.toString(args));

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
							throw new IllegalArgumentException(Arrays.toString(arguments));
						}
					}
				}

				return sub.executeSubCommand(sender, arguments);
			}
		}

		return false;
	}


	private static boolean compare(boolean caseSensitive, String o1, String o2) {
		val r = caseSensitive ? o1.equals(o2) : o1.equalsIgnoreCase(o2);
		log.info("COMPARE: " + caseSensitive + " : " + o1 + " : " + o2 + " = " + r);
		return r;
	}

	private static String[] trimArgs(int number, String ... args) {

		val lim = Math.max(0, Math.min(number, args.length - 1));
		val arr = new String[number];

		//  0  1  2  3  4
		// [a, 3, 4, 5, 6]: 5
		//    [3, 4, 5, 6]: 4 -> lim

		// [b, 3, 4, 5, 6]: 5
		//    [3, 4, 5]   : 3 -> lim

		// [c, 2, 4]      : 3 -> lim
		//    [2, 4, -, -]: 4

		System.arraycopy(args, 1, arr, 0, lim);

		log.info("IN ARGS: " + Arrays.toString(args));
		log.info("OUT ARGS: " + Arrays.toString(arr));
		return arr;
	}
}

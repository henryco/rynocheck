package net.henryco.rynocheck.command.sub.currency;


import com.github.henryco.injector.meta.annotations.Provide;
import lombok.val;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.DaoBundle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;


@Provide("SCSetCurr") @Singleton
public class CurrencySetSubCommand implements RynoCheckSubCommand {

	private static final String NAME = "NAME";
	private static final String CODE = "CODE";
	private static final String MICRO = "MICRO";
	private static final String FEE = "FEE";
	private static final String EMITTER = "EMITTER";

	private final CommandContext commandContext;
	private final DaoBundle daoBundle;

	@Inject
	public CurrencySetSubCommand(CommandContext commandContext,
								 DaoBundle daoBundle) {
		this.commandContext = commandContext;
		this.daoBundle = daoBundle;
	}

	@Override
	public int maxNumberOfArgs() {
		return 3;
	}

	@Override
	public String name() {
		return "set";
	}

	// set {code} {attribute} {new value}
	// attribute: <micro> <fee> <emitter>
	@Override
	public boolean executeSubCommand(CommandSender sender, String[] args) {


		if (args.length < 4) return false;
		if (args.length % 2 != 0) return false;

		val currency = daoBundle.getCurrencyDao().getCurrencyByCode(args[1]);
		if (currency == null) {
			sender.sendMessage("Unknown currency: " + args[1]);
			return true;
		}

		val player = (Player) sender;
		val username = daoBundle.getSessionDao().getSessionName(player.getUniqueId());

		if (username == null || username.trim().isEmpty() || !username.equals(currency.getEmitter())) {
			sender.sendMessage("You are not emitter of this currency!");
			return true;
		}

		for (int i = 2; i < args.length - 1; i += 2) {

			if (args[i].equalsIgnoreCase(NAME) || args[i].equalsIgnoreCase(CODE)) {
				sender.sendMessage("You cannot change name or code!");
				return true;
			}

			switch (args[i].toUpperCase()) {

				case FEE:
					currency.setFee(new BigDecimal(new Double(args[i + 1]) / 100.D));
					break;

				case MICRO:
					currency.setMicroLimit(new BigDecimal(args[i + 1]));
					break;

				case EMITTER:
					currency.setEmitter(args[i + 1]);
					break;
			}

		}

		sender.sendMessage("You are going to edit " + currency.getName());
		commandContext.showDecisionQuestion(sender);
		commandContext.addPositive(player.getUniqueId(), aVoid -> {
			boolean updated = daoBundle.getCurrencyDao().updateCurrency(currency);
			sender.sendMessage(updated ? "Done!" : "Internal error");
			return true;
		});
		commandContext.addNegative(player.getUniqueId(), aVoid -> {
			sender.sendMessage("Editing canceled");
			return true;
		});
		return true;
	}
}
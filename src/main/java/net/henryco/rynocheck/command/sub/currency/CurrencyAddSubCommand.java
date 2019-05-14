package net.henryco.rynocheck.command.sub.currency;


import com.github.henryco.injector.meta.annotations.Provide;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.val;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.DaoBundle;
import net.henryco.rynocheck.data.model.Currency;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

@Provide("SCAddCurr") @Singleton
public class CurrencyAddSubCommand implements RynoCheckSubCommand {

	private final CommandContext commandContext;
	private final DaoBundle daoBundle;

	@Inject
	public CurrencyAddSubCommand(CommandContext commandContext,
								 DaoBundle daoBundle) {
		this.commandContext = commandContext;
		this.daoBundle = daoBundle;
	}


	@Override // args: add {name} {code} {micro} {fee} {emitter}
	public boolean executeSubCommand(CommandSender sender, String[] args) {

		val player = (Player) sender;
		if (args.length < 6) return false;

		val currency = new Currency();
		currency.setName(args[1]);
		currency.setMicroLimit(processDecimal(args[3]));


		val code = args[2].toUpperCase();
		if (code.length() != 3) {
			sender.sendMessage("Currency code should be 3 characters length");
			return true;
		}
		currency.setCode(code);


		BigDecimal fee = processDecimal(args[4]);
		if (fee != null) {
			if (fee.compareTo(new BigDecimal(100)) > 0 ||
					fee.compareTo(BigDecimal.ZERO) == 0) {
				sender.sendMessage("Fee should be bounded from 0 to 100");
				return true;
			}

			fee = new BigDecimal(new Double(fee.abs().toPlainString()) / 100D);
		}

		currency.setFee(fee);


		val emitter = processStr(args[5]);
		currency.setEmitter(emitter);


		val currencyDao = daoBundle.getCurrencyDao();
		if (currencyDao.isExists(currency)) {
			player.sendMessage("Currency already exist!");
			return true;
		}


		sender.sendMessage(" ");
		sender.sendMessage("You intend to create new currency: " + args[1]
				+ ", you will not be able to remove it in the future.");

		if (emitter == null || !emitter.equals(daoBundle.getSessionDao().getSessionName(player.getUniqueId()))) {
			player.sendMessage("There are no currency emitter or emitter is not you");
			player.sendMessage("If you continue, you will lose base level control over this currency");
		}

		commandContext.showDecisionQuestion(player);
		commandContext.addPositive(player.getUniqueId(), aVoid -> {
			boolean added = daoBundle.getCurrencyDao().addCurrency(currency);
			sender.sendMessage(added ? "Done!" : "Cannot add new currency");
			return true;
		});

		commandContext.addNegative(player.getUniqueId(), aVoid -> {
			sender.sendMessage("Creating canceled.");
			return true;
		});

		return true;
	}


	private static String processStr(String arg) {
		return arg.equalsIgnoreCase("null") ? null : arg;
	}

	private static BigDecimal processDecimal(String arg) {
		String v = processStr(arg);
		return v == null ? null : new BigDecimal(v).abs();
	}
}
package net.henryco.rynocheck.command.sub.currency;


import com.github.henryco.injector.meta.annotations.Provide;
import lombok.val;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.data.dao.DaoBundle;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.page.Page;
import net.henryco.rynocheck.utils.Util;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;


@Provide("SCListCurr") @Singleton
public class CurrencyListSubCommand implements RynoCheckSubCommand {

	private final DaoBundle daoBundle;

	@Inject
	public CurrencyListSubCommand(DaoBundle daoBundle) {
		this.daoBundle = daoBundle;
	}


	@Override
	public int maxNumberOfArgs() {
		return 2;
	}

	@Override
	public String name() {
		return "list";
	}

	/*
	 * list {emitter} {page}
	 * list {emitter}
	 * list {page}
	 * list <null>
	 */ @Override
	public boolean executeSubCommand(CommandSender sender, String[] args) {

	 	String emitter;
	 	long pageNumb = 1;

	 	if (args[0] == null)

	 		emitter = null;

	 	else try {

			pageNumb = Long.valueOf(args[0]);
			emitter = null;

		} catch (NumberFormatException e) {

	 		emitter = args[0];

	 		if (args.length == 3) {
	 			pageNumb = Long.valueOf(args[1]);
			}
		}

		pageNumb = Math.max(1, pageNumb);

		val currencies = daoBundle.getCurrencyDao().getCurrencies(emitter, Page.factory.ninePage(pageNumb - 1));
		showCurrencies(currencies, sender, pageNumb);

		return true;
	}


	private static void showCurrencies(List<Currency> currencies,
									   CommandSender sender,
									   long page) {

		sender.sendMessage(" "); // clearing console
		sender.sendMessage(" "); // clearing console

	 	sender.sendMessage("List of currencies [page: " + page + "]:");
	 	if (currencies == null || currencies.isEmpty()) {
	 		sender.sendMessage(EMPTY_MESSAGE);
	 		return;
		}

		currencies.forEach(c -> {

			// CODE | NAME | MICRO: {micro} | FEE: {fee}% | EMITTER
			// ANC | AncapCoin | MICRO: 100 | FEE: 1% | henryco

			String block1 = " " +c.getCode() + " ";
			String block2 = " " + c.getName() + " ";
			String block3 = createMicro(c.getMicroLimit());
			String block4 = createFee(c.getFee());
			String block5 = createEmitter(c.getEmitter());

			sender.sendMessage(" *" + block1 + "|" + block2 + "|" + block3 + "|" + block4 + "|" + block5);
		});

	}

	private static String createEmitter(String emitter) {
	 	return " " + (emitter == null ? "" : emitter);
	}

	private static String createMicro(BigDecimal micro) {
		return " M: " + (micro == null ? "N/A" : Util.precise(micro.toString())) + " ";
	}

	private static String createFee(BigDecimal fee) {
	 	return " F: " + (fee == null
				? "0"
				: Util.precise(fee.multiply(new BigDecimal(100)).toString())
		) + "% ";
	}
}
package net.henryco.rynocheck.command.sub.wallet;


import com.github.henryco.injector.meta.annotations.Provide;
import lombok.extern.java.Log;
import lombok.val;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.data.dao.DaoBundle;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyBalance;
import net.henryco.rynocheck.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.List;


@Provide("SCBlns") @Singleton @Log
public class WalletsBalanceSubCommand implements RynoCheckSubCommand {

	private final DaoBundle daoBundle;

	// TODO: REPLACE manual pagination with dao implementation

	@Inject
	public WalletsBalanceSubCommand(DaoBundle daoBundle) {
		this.daoBundle = daoBundle;
	}


	@Override
	public int maxNumberOfArgs() {
		return 1;
	}

	@Override
	public String name() {
		return "balance";
	}

	/* args: {currency}
	 * args: {page}
	 * args: <null>
	 */ @Override
	public boolean executeSubCommand(CommandSender commandSender, String[] args) {

		val player = (Player) commandSender;
		val sender = daoBundle.createUserSession(player);
		if (sender == null) return true;

		val moneyBalances = daoBundle.getBalanceDao().getUserBalanceList(sender);
		val argument = args[0];

		if (argument == null) { // null case
			showBalance(moneyBalances, player, 1);
			return true;
		}

		try {
			showBalance(moneyBalances, player, Math.max(1, Integer.valueOf(argument)));
			return true;
		} catch (NumberFormatException e) {

			val currency = daoBundle.getCurrencyDao().getCurrencyByCode(argument.toUpperCase());
			if (currency == null) {
				player.sendMessage("Unknown currency: " + argument);
				return true;
			}

			val balance = daoBundle.getBalanceDao().getUserBalance(sender, currency.getId());

			player.sendMessage(currency.getName() + " balance: ");
			if (balance == null) {
				showEmpty(player);
				return true;
			}

			showCurrency(player, 1, currency, balance.getAmount().toString());
		}

	 	return true;
	}


	private void showBalance(List<MoneyBalance> balances, Player player, int page) {

		player.sendMessage(" "); // clearing console
		player.sendMessage(" "); // clearing console

	 	double d = ((double) balances.size()) / ((double) PAGE_SIZE);
		int pages = Math.max((int) Math.ceil(d), 1);
		int actual = (Math.min(page, pages) - 1) * PAGE_SIZE;

		player.sendMessage("Currency wallet balance [page " + (actual + 1) + "/" + pages+"]: ");
		int i = 0;

		if (balances.isEmpty()) showEmpty(player);

		for (int k = actual, bSize = balances.size(); k < bSize; k++) {
			MoneyBalance b = balances.get(k);

			if (i++ >= PAGE_SIZE) break;

			try {

				val currency = daoBundle.getCurrencyDao().queryForId(b.getCurrency());
				showCurrency(player, i, currency, b.getAmount().toString());

			} catch (SQLException e) {
				log.throwing(getClass().getName(), "showBalance", e);
			}
		}
	}

	private static void showCurrency(Player player, int i, Currency c, String amount) {
		player.sendMessage(" +" + i + "+ " + c.getName() + ": " + Util.precise(amount) + " " + c.getCode());
	}

	private static void showEmpty(Player player) {
		player.sendMessage(EMPTY_MESSAGE);
	}

}
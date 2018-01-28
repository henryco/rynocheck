package net.henryco.rynocheck.command.wallet.sub;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.data.dao.DaoBundle;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyTransaction;
import net.henryco.rynocheck.data.page.Page;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Component("SCHist") @Singleton
public class WalletHistorySubCommand implements WalletSubCommand {

	private static final DateFormat DATE_FORMAT
			= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final DaoBundle daoBundle;

	@Inject
	public WalletHistorySubCommand(DaoBundle daoBundle) {
		this.daoBundle = daoBundle;
	}

	/* args: history {currency} {page}
	 * args: history {currency}
	 * args: history {page}
	 * args: history
	 */ @Override
	public boolean executeSubCommand(CommandSender commandSender, String[] args) {

	 	if (args.length == 0) return false;

		val player = (Player) commandSender;
		val user = daoBundle.getSessionDao().getSessionName(player.getUniqueId());
		if (!daoBundle.getAccountDao().isAccountExists(user)) {
			player.sendMessage("Account session error: null");
			return true;
		}

		Currency currency;
		long pageNumb = 1;
		Page page;

		if (args.length == 1)

			currency = null;

		else try {

			pageNumb = Long.valueOf(args[1]);
			currency = null;

		} catch (NumberFormatException e) {

			currency = daoBundle.getCurrencyDao().getCurrencyByCode(args[1].toUpperCase());
			if (currency == null && !args[1].equals(ALL)) {
				commandSender.sendMessage("Unknown currency: " + args[1]);
				return true;
			}

			if (args.length == 3) {
				pageNumb = Long.valueOf(args[2]);
			}
		}


		pageNumb = Math.max(1, pageNumb);
		page = Page.factory.ninePage(pageNumb - 1);


		val transactions = getTransactions(user, page, currency);
		showHistory(transactions, commandSender, user, pageNumb);

		return true;
	}


	private List<MoneyTransaction> getTransactions(String user, Page page, Currency currency) {

		val dao = daoBundle.getTransactionDao();

		if (currency == null) return dao.getUserTransactions(user, page);
		else return dao.getUserTransactions(user, currency, page);
	}


	private static void showHistory(List<MoneyTransaction> history,
									CommandSender sender,
									String user,
									long page) {

	 	sender.sendMessage("Transaction history [page " + page + "]:");

	 	if (history == null || history.isEmpty()) {
			sender.sendMessage("+EMPTY+");
			return;
		}

		history.forEach(h -> {

			// yyyy-MM-dd HH:mm:ss | AMOUNT | <-- RECEIVER | DESCRIPTION
			// 2018-01-28 01:10:22 | 100 ANC | <-- henryco | regular

			String block1 = DATE_FORMAT.format(h.getTime()) + " ";
			String block2 = createAmountField(h.getSender(), user, h.getAmount(), h.getCurrencyCode());
			String block3 = createNameField(h.getSender(), h.getReceiver(), user);
			String block4 = h.getDescription();

			sender.sendMessage(block1 + "|" + block2 + "|" + block3 + "|" + block4);
		});

	}

	private static String createNameField(String sender, String receiver, String user) {
	 	return sender.equals(user) ? " --> " + receiver + " " : " <-- " + sender + " ";
	}

	private static String createAmountField(String sender, String user, BigDecimal amount, String code) {
		return " " + (sender.equals(user) ? "-" : "+") + amount.toString() + " " + code + " ";
	}
}
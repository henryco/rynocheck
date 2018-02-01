package net.henryco.rynocheck.command.sub.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.data.dao.DaoBundle;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyTransaction;
import net.henryco.rynocheck.data.page.Page;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static net.henryco.rynocheck.data.model.MoneyTransaction.TAG_FEE;

@Component("SCHist")
@Singleton
public class WalletHistorySubCommand implements RynoCheckSubCommand {

	private static final DateFormat DATE_FORMAT
			= new SimpleDateFormat("ddMMyy HH:mm");

	private final DaoBundle daoBundle;

	@Inject
	public WalletHistorySubCommand(DaoBundle daoBundle) {
		this.daoBundle = daoBundle;
	}

	/* args: history {currency} {page}
	 * args: history {currency}
	 * args: history {page}
	 * args: history
	 */
	@Override
	public boolean executeSubCommand(CommandSender commandSender, String[] args) {

		if (args.length == 0) return false;

		val player = (Player) commandSender;
		val user = daoBundle.createUserSession(player);
		if (user == null) return true;

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
		else return dao.getUserTransactions(user, currency.getId(), page);
	}


	private static void showHistory(List<MoneyTransaction> history,
									CommandSender sender,
									String user,
									long page) {

		sender.sendMessage(" "); // clearing console
		sender.sendMessage(" "); // clearing console

		sender.sendMessage("Transaction history [page " + page + "]:");

		if (history == null || history.isEmpty()) {
			sender.sendMessage(EMPTY_MESSAGE);
			return;
		}

		history.forEach(transaction -> {

			// yyyy-MM-dd HH:mm:ss | DES | CODE | AMOUNT | <-- RECEIVER
			// 2018-01-28 01:10:22 | REG | ANC | 100 | <-- henryco

			String block1 = createTimeField(transaction);
			String block2 = createCurrencyField(transaction);
			String block3 = createAmountField(transaction, user);
			String block4 = createNameField(transaction, user);
			String block5 = creteTagField(transaction);

			sender.sendMessage(" *" + block1 + "|" + block5 + "|" + block2 + "|" + block3 + "|" + block4);
		});

	}

	private static String createCurrencyField(MoneyTransaction transaction) {
		return " " + transaction.getCurrencyCode() + " ";
	}

	private static String createTimeField(MoneyTransaction transaction) {
		return " " + DATE_FORMAT.format(transaction.getTime()) + " ";
	}

	private static String creteTagField(MoneyTransaction transaction) {
		return " " + transaction.getDescription().substring(0, 3).toUpperCase() + " ";
	}

	private static String createNameField(MoneyTransaction transaction, String user) {

		final String receiver;
		final String sender;

		if (transaction.getDescription().equalsIgnoreCase(TAG_FEE)) {
			receiver = sender = Currency.createBankName(transaction.getCurrencyCode());
		} else {
			receiver = transaction.getReceiver();
			sender = transaction.getSender();
		}

		char[] symbol = {'>', '<'};
		if (user.equals(transaction.getSender())) symbol[1] = '>';
		if (user.equals(transaction.getReceiver())) symbol[0] = '<';

		return " " + new String(symbol) + " " + (transaction.getSender().equals(user) ? receiver : sender) + " ";
	}

	private static String createAmountField(MoneyTransaction transaction, String user) {

		val sender = transaction.getSender();
		val receiver = transaction.getReceiver();
		val amount = transaction.getAmount();

		final String sign;
		if (user.equals(sender) && user.equals(receiver))
			sign = "+-";
		else if (user.equals(sender)) sign = "-";
		else sign = "+";

		return " " + sign + amount.toString() + " ";
	}
}
package net.henryco.rynocheck.data.dao;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import lombok.Data;
import lombok.val;
import net.henryco.rynocheck.data.dao.account.MoneyAccountDao;
import net.henryco.rynocheck.data.dao.balance.MoneyBalanceDao;
import net.henryco.rynocheck.data.dao.currency.CurrencyDao;
import net.henryco.rynocheck.data.dao.session.SessionDao;
import net.henryco.rynocheck.data.dao.transaction.MoneyTransactionDao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyBalance;
import net.henryco.rynocheck.utils.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@Data @Component
public class DaoBundle {

	private final MoneyTransactionDao transactionDao;
	private final MoneyBalanceDao balanceDao;
	private final MoneyAccountDao accountDao;
	private final CurrencyDao currencyDao;
	private final SessionDao sessionDao;
	private final Plugin plugin;

	@Inject
	public DaoBundle(MoneyTransactionDao transactionDao,
					 MoneyBalanceDao balanceDao,
					 MoneyAccountDao accountDao,
					 CurrencyDao currencyDao,
					 SessionDao sessionDao,
					 Plugin plugin) {
		this.transactionDao = transactionDao;
		this.balanceDao = balanceDao;
		this.accountDao = accountDao;
		this.currencyDao = currencyDao;
		this.sessionDao = sessionDao;
		this.plugin = plugin;
	}


	public String createUserSession(Player player) {
		String user = getSessionDao().getSessionName(player.getUniqueId());
		if (!getAccountDao().isAccountExists(user)) {
			player.sendMessage("Account session error: null");
			return null;
		}
		return user;
	}

	public String createRecipient(Player player, String arg) {
		if (!getAccountDao().isAccountExists(arg)) {
			player.sendMessage("Unknown recipient!");
			return null;
		}
		return arg;
	}

	public Currency createCurrency(Player player, String arg) {
		val currency = getCurrencyDao().getCurrencyByCode(arg.toUpperCase());
		if (currency == null) {
			player.sendMessage("Unknown currency!");
			return null;
		}
		return currency;
	}

	public MoneyBalance createBalance(Player player, String sender, Currency currency) {
		val senderBalance = getBalanceDao().getUserBalance(sender, currency.getId());
		if (senderBalance == null) {
			player.sendMessage("You don't have any funds");
			getBalanceDao().createNewOne(sender, currency.getId());
			return null;
		}
		return senderBalance;
	}


	public void sendMsgToRec(Player player, String sender, String recipient,
							 String amount, String currency) {

		UUID id = getSessionDao().getSessionId(recipient);
		if (id == null) return;

		Player p = plugin.getServer().getPlayer(id);
		if (p == null) return;

		p.sendMessage("Received founds from " + sender);
		player.sendMessage("Wallet balance: " + StringUtil.precise(amount) + " " + currency);
	}
}
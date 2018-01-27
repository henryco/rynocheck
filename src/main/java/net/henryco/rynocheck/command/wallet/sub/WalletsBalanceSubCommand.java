package net.henryco.rynocheck.command.wallet.sub;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.data.dao.account.MoneyAccountDao;
import net.henryco.rynocheck.data.dao.currency.CurrencyDao;
import net.henryco.rynocheck.data.dao.session.SessionDao;
import net.henryco.rynocheck.data.dao.wallet.MoneyBalanceDao;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@Component("SCBlns") @Singleton
public class WalletsBalanceSubCommand implements WalletSubCommand {

	private final MoneyBalanceDao balanceDao;
	private final MoneyAccountDao accountDao;
	private final CurrencyDao currencyDao;
	private final SessionDao sessionDao;


	@Inject
	public WalletsBalanceSubCommand(MoneyBalanceDao balanceDao,
									MoneyAccountDao accountDao,
									CurrencyDao currencyDao,
									SessionDao sessionDao) {
		this.balanceDao = balanceDao;
		this.accountDao = accountDao;
		this.currencyDao = currencyDao;
		this.sessionDao = sessionDao;
	}


	/* args: balance {currency} {page}
	 * args: balance {currency}
	 * args: balance
	 */ @Override
	public boolean executeSubCommand(CommandSender commandSender, String[] args) {

		val player = (Player) commandSender;

		val sender = sessionDao.getSessionName(player.getUniqueId());
		if (!accountDao.isAccountExists(sender)) {
			player.sendMessage("Account session error: null");
			return true;
		}

		if (args.length == 1) {
			val moneyBalances = balanceDao.getUserBalanceList(sender);


		}

	 	return true;
	}
}
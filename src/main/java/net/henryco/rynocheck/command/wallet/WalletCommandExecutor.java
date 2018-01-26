package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;

import net.henryco.rynocheck.data.dao.account.MoneyAccountDao;
import net.henryco.rynocheck.data.dao.currency.CurrencyDao;
import net.henryco.rynocheck.data.dao.session.SessionDao;
import net.henryco.rynocheck.data.dao.wallet.MoneyBalanceDao;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyBalance;
import net.henryco.rynocheck.service.IMoneyTransactionService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * @author Henry on 12/01/18.
 */ @Component @Singleton
public class WalletCommandExecutor extends RynoCheckExecutor {

 	private static final String SEND = "send";
	private static final String BALANCE = "balance";
	private static final String HISTORY = "history";

	private final IMoneyTransactionService transactionService;
 	private final CommandContext commandContext;
 	private final MoneyBalanceDao balanceDao;
 	private final MoneyAccountDao accountDao;
	private final CurrencyDao currencyDao;
	private final SessionDao sessionDao;

	@Inject
	public WalletCommandExecutor(IMoneyTransactionService transactionService,
								 CommandContext commandContext,
								 MoneyAccountDao accountDao,
								 MoneyBalanceDao balanceDao,
								 CurrencyDao currencyDao,
								 SessionDao sessionDao,
								 Plugin plugin) {

		super(commandContext, plugin, "wallet");

		this.transactionService = transactionService;
		this.commandContext = commandContext;
		this.sessionDao = sessionDao;
		this.accountDao = accountDao;
		this.balanceDao = balanceDao;
		this.currencyDao = currencyDao;
	}

	@Override
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		if (args.length == 0) return false;
		if (!(sender instanceof Player)) {
			// todo
			return true;
		}

		switch (args[0]) {
			case SEND: return send((Player) sender, args);
			case BALANCE: return balance((Player) sender, args);
			case HISTORY: return history((Player) sender, args);
		}

		return false;
	}


	// args: send {recipient} {amount} {currency}
	private boolean send(Player player, String[] args) {

		if (args.length != 4) return false;

		val sender = sessionDao.getSessionName(player.getUniqueId());
		if (!accountDao.isAccountExists(sender)) {
			player.sendMessage("Account session error: null");
			return true;
		}

		val recipient = args[1];
		if (!accountDao.isAccountExists(recipient)) {
			player.sendMessage("Unknown recipient!");
			return true;
		}

		val currency = currencyDao.getCurrencyByCode(args[3]);
		if (currency == null) {
			player.sendMessage("Unknown currency!");
			return true;
		}

		val senderBalance = balanceDao.getUserBalance(currency, sender);
		if (senderBalance == null) {
			player.sendMessage("You don't have any founds");
			balanceDao.createNewOne(sender, currency);
			return true;
		}

		final BigDecimal amount; try {
			amount = new BigDecimal(args[2]);
		} catch (NumberFormatException e) {
			player.sendMessage("Wrong currency amount!");
			return true;
		}

		MoneyBalance receiverBalance = balanceDao.getUserBalance(currency, recipient);
		if (receiverBalance == null) {
			receiverBalance = balanceDao.createNewOne(recipient, currency);
		}

		val rBalanceAmount = receiverBalance.getAmount();
		val sBalanceAmount = senderBalance.getAmount();
		if (sBalanceAmount.compareTo(amount) < 0) {
			player.sendMessage("You don't have enough founds!");
			return true;
		}

		val fReceiverBalance = receiverBalance;

		Runnable delayedCalculation = () -> {

			transactionService.calculateTransactions(sender, currency,
					(BigDecimal result, Throwable throwable) -> {
				if (result.compareTo(senderBalance.getAmount()) != 0) {
					senderBalance.setAmount(result);
					sendMsgToSnd(player, result, currency);
				}
			});

			transactionService.calculateTransactions(recipient, currency,
					(BigDecimal result, Throwable throwable) -> {
				if (result.compareTo(fReceiverBalance.getAmount()) != 0) {
					fReceiverBalance.setAmount(result);
					sendMsgToRec(sender, recipient, player, result, currency);
				}
			});
		};


		val microLimit = currency.getMicroLimit();
		if (microLimit == null || amount.compareTo(microLimit) <= 0) {

			commandContext.addPositive(player.getUniqueId(), aVoid -> {

				fReceiverBalance.setAmount(rBalanceAmount.add(amount));
				senderBalance.setAmount(sBalanceAmount.subtract(amount));

				sendMsgToSnd(player, senderBalance.getAmount(), currency);
				sendMsgToRec(sender, recipient, player, fReceiverBalance.getAmount(), currency);

				delayedCalculation.run();
				return true;
			});

			return true;
		}

		commandContext.addPositive(player.getUniqueId(), aVoid -> {
			delayedCalculation.run();
			return true;
		});

		return true;
	}


	private boolean balance(Player player, String[] args) {

		return true;
	}

	private boolean history(Player player, String[] args) {

		return true;
	}




	private void sendMsgToSnd(Player player, BigDecimal amount, Currency currency) {
		player.sendMessage("Wallet balance: " + amount.toString() + " " + currency.getCode());
	}

	private void sendMsgToRec(String sender, String recipient, Player player,
							  BigDecimal amount, Currency currency) {
		UUID id = sessionDao.getSessionId(recipient);
		if (id == null) return;

		Player p = getPlugin().getServer().getPlayer(id);
		if (p == null) return;

		p.sendMessage("Received founds from " + sender);
		player.sendMessage("Wallet balance: " + amount.toString() + " " + currency.getCode());
	}
}
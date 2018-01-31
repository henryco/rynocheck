package net.henryco.rynocheck.command.sub.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.DaoBundle;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyTransaction;
import net.henryco.rynocheck.service.IMoneyTransactionService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Date;

import static net.henryco.rynocheck.data.model.MoneyTransaction.TAG_REGULAR;
import static net.henryco.rynocheck.service.IMoneyTransactionService.Notification;


@Component("SCSend") @Singleton
public class WalletSendSubCommand implements RynoCheckSubCommand {

	private final IMoneyTransactionService transactionService;
	private final CommandContext commandContext;
	private final DaoBundle daoBundle;

	@Inject
	public WalletSendSubCommand(IMoneyTransactionService transactionService,
								CommandContext commandContext,
								DaoBundle daoBundle) {
		this.transactionService = transactionService;
		this.commandContext = commandContext;
		this.daoBundle = daoBundle;
	}

	@Override // args: send {recipient} {amount} {currency}
	public boolean executeSubCommand(CommandSender commandSender, String[] args) {

		val player = (Player) commandSender;
		if (args.length != 4) return false;

		val sender = daoBundle.createUserSession(player);
		if (sender == null) return true;

		val recipient = daoBundle.createRecipient(player, args[1]);
		if (recipient == null) return true;

		val currency = daoBundle.createCurrency(player, args[3]);
		if (currency == null) return true;

		val senderBalance = daoBundle.createBalance(player, sender, currency);
		if (senderBalance == null) return true;

		final BigDecimal amount; try {
			if (args[2].equals(ALL)) {
				amount = senderBalance.getAmount();
				if (amount.compareTo(new BigDecimal(0)) < 0) {
					player.sendMessage("You don't have any funds!");
					return true;
				}
			} else amount = new BigDecimal(args[2]).abs();

		} catch (NumberFormatException e) {
			player.sendMessage("Wrong currency amount!");
			return true;
		}


		player.sendMessage("Are you want to send to " + recipient + " ");
		player.sendMessage(amount.toString() + currency.getCode() + calcFee(amount, currency) +  "? /< y | N >");


		commandContext.addPositive(player.getUniqueId(), aVoid -> {

			MoneyTransaction transaction = new MoneyTransaction();
			transaction.setAmount(amount);
			transaction.setDescription(TAG_REGULAR);
			transaction.setCurrency(currency);
			transaction.setSender(sender);
			transaction.setReceiver(recipient);
			transaction.setTime(new Date(System.currentTimeMillis()));

			Notification notification = createNotification(player, daoBundle, sender, recipient);
			transactionService.releaseTransaction(transaction, notification, true);

			return true;
		});

		commandContext.addNegative(player.getUniqueId(), aVoid -> {
			player.sendMessage("Transaction canceled");
			return true;
		});

		return true;
	}


	private static void sendMsgToSnd(Player player, String amount, String currency) {
		player.sendMessage("Updated wallet balance: " + amount + " " + currency);
	}


	private static String calcFee(BigDecimal amount, Currency currency) {
		if (currency.getFee() == null) return "";

		val fee = amount.multiply(currency.getFee());
		val pct = currency.getFee().multiply(new BigDecimal(100));

		return " + " + fee.toString() + currency.getCode()
				+ " (FEE: " + pct.toString() + "%) ";
	}


	private static Notification createNotification(Player player, DaoBundle bundle,
												   String sender, String recipient) {
		return new Notification() {

			@Override
			public void success(String userName, String amount, String currency) {

				if (userName.equals(sender))
					sendMsgToSnd(player, amount, currency);
				if (userName.equals(recipient))
					bundle.sendMsgToRec(player, sender, recipient, amount, currency);
			}

			@Override
			public void error(String userName, String amount, String currency) {

				if (userName.equals(sender))
					player.sendMessage("Transaction aborted, not enough funds: " + amount + " " + currency);
			}
		};
	}

}
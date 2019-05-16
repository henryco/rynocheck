package net.henryco.rynocheck.command.sub.wallet;


import com.github.henryco.injector.meta.annotations.Provide;
import lombok.val;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.DaoBundle;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyTransaction;
import net.henryco.rynocheck.transaction.IMoneyTransactionService;
import net.henryco.rynocheck.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Calendar;

import static net.henryco.rynocheck.context.CommandContext.YN_OPTION;
import static net.henryco.rynocheck.data.model.MoneyTransaction.TAG_REGULAR;
import static net.henryco.rynocheck.transaction.IMoneyTransactionService.Notification;


@Provide("SCSend") @Singleton
public class WalletSendSubCommand implements RynoCheckSubCommand {

	private final IMoneyTransactionService transactionService;
	private final CommandContext commandContext;
	private final DaoBundle daoBundle;
	private final boolean selfEnable;

	@Inject
	public WalletSendSubCommand(IMoneyTransactionService transactionService,
								@Named("selfEnable") boolean selfEnable,
								CommandContext commandContext,
								DaoBundle daoBundle) {
		this.transactionService = transactionService;
		this.commandContext = commandContext;
		this.daoBundle = daoBundle;
		this.selfEnable = selfEnable;
	}

	@Override
	public int maxNumberOfArgs() {
		return 3;
	}

	@Override
	public String name() {
		return "send";
	}

	@Override
	public boolean strict() {
		return true;
	}

	@Override // args: {recipient} {amount} {currency}
	public boolean executeSubCommand(CommandSender commandSender, String[] args) {

		val player = (Player) commandSender;

		val sender = daoBundle.createUserSession(player);
		if (sender == null) return true;

		val recipient = daoBundle.createRecipient(player, args[0]);
		if (recipient == null) return true;

		if (recipient.equals(sender) && !selfEnable) {
			player.sendMessage("Self transactions are not supported!");
			return true;
		}

		val currency = daoBundle.createCurrency(player, args[2]);
		if (currency == null) return true;

		val senderBalance = daoBundle.createBalance(player, sender, currency);
		if (senderBalance == null) return true;

		final BigDecimal amount; try {
			if (args[1].equals(ALL)) {
				amount = senderBalance.getAmount();
				if (amount.compareTo(BigDecimal.ZERO) < 0) {
					player.sendMessage("You don't have any funds!");
					return true;
				}
			} else amount = new BigDecimal(args[1]).abs();

		} catch (NumberFormatException e) {
			player.sendMessage("Wrong currency amount!");
			return true;
		}

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			player.sendMessage("Cannot handle empty transactions!");
			return true;
		}

		player.sendMessage("Are you want to send to " + recipient + " ");
		player.sendMessage(amount.toString() + currency.getCode() + calcFee(amount, currency) + YN_OPTION);


		commandContext.addPositive(player.getUniqueId(), aVoid -> {

			MoneyTransaction transaction = new MoneyTransaction();
			transaction.setAmount(amount);
			transaction.setDescription(TAG_REGULAR);
			transaction.setCurrency(currency);
			transaction.setSender(sender);
			transaction.setReceiver(recipient);
			transaction.setTime(Calendar.getInstance().getTime());

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
		player.sendMessage("Updated wallet balance: " + Util.precise(amount) + " " + currency);
	}


	private static String calcFee(BigDecimal amount, Currency currency) {
		if (currency.getFee() == null) return "";

		val fee = amount.multiply(currency.getFee());
		val pct = currency.getFee().multiply(new BigDecimal(100));

		return " + " + Util.precise(fee.toString()) + currency.getCode()
				+ " (FEE: " + Util.precise(pct.toString()) + "%) ";
	}


	private static Notification createNotification(Player player, DaoBundle bundle,
												   String sender, String recipient) {
		return new Notification() {

			@Override
			public void success(String userName, String amount, String currency) {

				final String amn = Util.precise(amount);
				if (userName.equals(sender))
					sendMsgToSnd(player, amn, currency);
				if (userName.equals(recipient))
					bundle.sendMsgToRec(player, sender, recipient, amn, currency);
			}

			@Override
			public void error(String userName, String amount, String currency) {

				if (userName.equals(sender))
					player.sendMessage("Transaction aborted, not enough funds: " + Util.precise(amount) + " " + currency);
			}
		};
	}

}
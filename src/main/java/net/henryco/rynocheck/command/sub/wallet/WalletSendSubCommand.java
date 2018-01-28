package net.henryco.rynocheck.command.sub.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.DaoBundle;
import net.henryco.rynocheck.data.model.Currency;
import net.henryco.rynocheck.data.model.MoneyBalance;
import net.henryco.rynocheck.data.model.MoneyTransaction;
import net.henryco.rynocheck.service.IMoneyTransactionService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Component("SCSend") @Singleton
public class WalletSendSubCommand implements RynoCheckSubCommand {

	private final IMoneyTransactionService transactionService;
	private final CommandContext commandContext;
	private final DaoBundle daoBundle;
	private final Plugin plugin;

	@Inject
	public WalletSendSubCommand(IMoneyTransactionService transactionService,
								CommandContext commandContext,
								DaoBundle daoBundle,
								Plugin plugin) {
		this.transactionService = transactionService;
		this.commandContext = commandContext;
		this.daoBundle = daoBundle;
		this.plugin = plugin;
	}

	@Override // args: send {recipient} {amount} {currency}
	public boolean executeSubCommand(CommandSender commandSender, String[] args) {

		val player = (Player) commandSender;
		if (args.length != 4) return false;

		val sender = daoBundle.getSessionDao().getSessionName(player.getUniqueId());
		if (!daoBundle.getAccountDao().isAccountExists(sender)) {
			player.sendMessage("Account session error: null");
			return true;
		}

		val recipient = args[1];
		if (!daoBundle.getAccountDao().isAccountExists(recipient)) {
			player.sendMessage("Unknown recipient!");
			return true;
		}

		val currency = daoBundle.getCurrencyDao().getCurrencyByCode(args[3].toUpperCase());
		if (currency == null) {
			player.sendMessage("Unknown currency!");
			return true;
		}

		val senderBalance = daoBundle.getBalanceDao().getUserBalance(sender, currency);
		if (senderBalance == null) {
			player.sendMessage("You don't have any founds");
			daoBundle.getBalanceDao().createNewOne(sender, currency);
			return true;
		}

		final BigDecimal amount; try {
			if (args[2].equals(ALL)) {
				amount = senderBalance.getAmount();
			} else amount = new BigDecimal(args[2]);

		} catch (NumberFormatException e) {
			player.sendMessage("Wrong currency amount!");
			return true;
		}

		MoneyBalance receiverBalance = daoBundle.getBalanceDao().getUserBalance(recipient, currency);
		if (receiverBalance == null) {
			receiverBalance = daoBundle.getBalanceDao().createNewOne(recipient, currency);
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

				if (throwable != null) {
					throwable.printStackTrace();
					plugin.getLogger().throwing(this.getClass().getName(),
							"calculateTransactions", throwable);
					return;
				}

				if (result != null) {

					BigDecimal finalResult = result.subtract(amount);
					if (finalResult.compareTo(currency.getMicroLimit().negate()) < 0) {
						player.sendMessage("Transaction aborted, not enough founds ("
								+ result.toString() + " " + currency.getCode() + ")");
						return;
					}

					MoneyTransaction transaction = new MoneyTransaction();
					transaction.setAmount(amount);
					transaction.setDescription("regular");
					transaction.setCurrency(currency.getId());
					transaction.setCurrencyCode(currency.getCode());
					transaction.setSender(sender);
					transaction.setReceiver(recipient);
					transaction.setTime(new Date(System.currentTimeMillis()));
					daoBundle.getTransactionDao().saveTransaction(transaction);

					if (currency.getFee() != null && !currency.getFee().equals(new BigDecimal(0))) {
						MoneyTransaction fee = new MoneyTransaction();
						fee.setAmount(amount.multiply(currency.getFee()));
						fee.setDescription("fee");
						fee.setCurrency(currency.getId());
						fee.setCurrencyCode(currency.getCode());
						fee.setSender(sender);
						fee.setReceiver(currency.getFeeRecipient());
						fee.setTime(new Date(System.currentTimeMillis()));
						daoBundle.getTransactionDao().saveTransaction(fee);

						finalResult = finalResult.subtract(fee.getAmount());
					}

					if (finalResult.compareTo(senderBalance.getAmount()) != 0) {
						senderBalance.setAmount(finalResult);
						sendMsgToSnd(player, finalResult, currency);
					}

				}
			});

			transactionService.calculateTransactions(recipient, currency,
					(BigDecimal result, Throwable throwable) -> {

				if (throwable != null) {
					throwable.printStackTrace();
					plugin.getLogger().throwing(this.getClass().getName(),
							"calculateTransactions", throwable);
					return;
				}

				if (result != null && result.compareTo(fReceiverBalance.getAmount()) != 0) {
					fReceiverBalance.setAmount(result);
					sendMsgToRec(sender, recipient, player, result, currency);
				}
			});

		};

		player.sendMessage("Are you want to send "
				+ amount.toString() + currency.getCode()
				+ calcFee(amount, currency)
				+ " to " + recipient + "? /<y|N>"
		);

		commandContext.addNegative(player.getUniqueId(), aVoid -> {
			player.sendMessage("Transaction canceled");
			return true;
		});

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


	private static String calcFee(BigDecimal amount, Currency currency) {
		if (currency.getFee() == null) return "";

		val fee = amount.multiply(currency.getFee());
		val pct = currency.getFee().multiply(new BigDecimal(100));

		return " + " + fee.toString() + currency.getCode()
				+ " (" + pct.toString() + "%) ";
	}

	private void sendMsgToSnd(Player player, BigDecimal amount, Currency currency) {
		player.sendMessage("Wallet balance: " + amount.toString() + " " + currency.getCode());
	}

	private void sendMsgToRec(String sender, String recipient, Player player,
							  BigDecimal amount, Currency currency) {
		UUID id = daoBundle.getSessionDao().getSessionId(recipient);
		if (id == null) return;

		Player p = plugin.getServer().getPlayer(id);
		if (p == null) return;

		p.sendMessage("Received founds from " + sender);
		player.sendMessage("Wallet balance: " + amount.toString() + " " + currency.getCode());
	}
}
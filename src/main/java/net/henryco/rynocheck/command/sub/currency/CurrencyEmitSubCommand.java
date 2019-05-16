package net.henryco.rynocheck.command.sub.currency;

import com.github.henryco.injector.meta.annotations.Provide;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.val;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.DaoBundle;
import net.henryco.rynocheck.data.model.MoneyTransaction;
import net.henryco.rynocheck.transaction.IMoneyTransactionService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.Date;

import static net.henryco.rynocheck.context.CommandContext.YN_OPTION;
import static net.henryco.rynocheck.data.model.MoneyTransaction.TAG_EMIT;

@Provide("SCEmitCurr") @Singleton
public class CurrencyEmitSubCommand implements RynoCheckSubCommand {

	private final IMoneyTransactionService transactionService;
	private final CommandContext commandContext;
	private final DaoBundle daoBundle;

	@Inject
	public CurrencyEmitSubCommand(IMoneyTransactionService transactionService,
								  CommandContext commandContext,
								  DaoBundle daoBundle) {
		this.transactionService = transactionService;
		this.commandContext = commandContext;
		this.daoBundle = daoBundle;
	}


	@Override
	public int maxNumberOfArgs() {
		return 3;
	}

	@Override
	public String name() {
		return "emit";
	}

	@Override // args: emit {recipient} {amount} {code}
	public boolean executeSubCommand(CommandSender sender, String[] args) {

		val player = (Player) sender;
	 	if (args.length < 4) return false;

	 	val user = daoBundle.createUserSession(player);
		if (user == null) return true;

		val recipient = daoBundle.createRecipient(player, args[1]);
		if (recipient == null) return true;

		val currency = daoBundle.createCurrency(player, args[3]);
		if (currency == null) return true;

		if (!user.equals(currency.getEmitter())) {
			player.sendMessage("You are not emitter of this currency!");
			return true;
		}

		val bank = currency.getBankName();

		MoneyTransaction transaction = new MoneyTransaction();
		transaction.setAmount(new BigDecimal(args[2]).abs());
		transaction.setDescription(TAG_EMIT);
		transaction.setCurrency(currency);
		transaction.setSender(bank);
		transaction.setReceiver(recipient);
		transaction.setTime(new Date(System.currentTimeMillis()));

		daoBundle.getPlugin().getLogger().info("Create emit transaction: " + transaction);

		player.sendMessage("Daily reminder: emitting money increases " +
				"man made inflation, which means stealth taxation. " +
				"But taxation is theft, are you sure to continue?" + YN_OPTION);

		commandContext.addNegative(player.getUniqueId(), aVoid -> {
			player.sendMessage("Money emitting canceled.");
			return true;
		});

		commandContext.addPositive(player.getUniqueId(), aVoid -> {

			player.sendMessage("Done! Transaction will be released in few moments");
			daoBundle.getPlugin().getLogger().info("Emit transaction in queue...");

			transactionService.releaseEmit(transaction, new IMoneyTransactionService.Notification() {

				@Override
				public void success(String userName, String amount, String currency) {
					if (userName.equals(recipient)) {

						player.sendMessage("Transaction update: " + recipient + " received "
								+ transaction.getAmount().toString() + " " + currency);
						daoBundle.sendMsgToRec(player, bank, recipient, amount, currency);
					}
				}

				@Override
				public void error(String userName, String amount, String currency) {
					player.sendMessage("Transaction fail: " + transaction);
				}
			});
			return true;
		});


		return true;
	}



}
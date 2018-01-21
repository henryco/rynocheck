package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.extern.java.Log;
import lombok.val;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.MoneyAccountDao;
import net.henryco.rynocheck.data.model.entity.MoneyAccount;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 15/01/18.
 */
@Component @Log @Singleton
public class WalletCreateCmEx extends RynoCheckExecutor {

	private final MoneyAccountDao moneyAccDao;

	@Inject
	public WalletCreateCmEx(CommandContext commandContext,
							MoneyAccountDao moneyAccDao,
							Plugin plugin) {
		super(commandContext, plugin);
		this.moneyAccDao = moneyAccDao;
	}

	@Override // <wallet-create> {name} {password} {optional email}
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		if (args.length == 0) return false;

		val account = new MoneyAccount();

		if (moneyAccDao.idExists(args[0].trim())) {
			sender.sendMessage("This name is already taken!");
			return true;
		} else account.setUid(args[0]);

		if (args.length < 2 || args[1].trim().isEmpty()) {
			sender.sendMessage("Password required");
			return true;
		} else account.setPass(args[1]);

		if (args.length == 3 && moneyAccDao.isEmailExists(args[2])) {
			sender.sendMessage("This email is already taken!");
			return true;
		} else if (args.length == 3) account.setEmail(args[2]);

		moneyAccDao.create(account);
		sender.sendMessage("Done, account: " + args[0] + " created, now you can login.");

		return true;
	}
}

package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.account.MoneyAccountDao;
import net.henryco.rynocheck.data.dao.session.WalletSessionDao;
import net.henryco.rynocheck.permission.RynoCheckPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 15/01/18.
 */
@Component @Singleton
public class WalletLoginCmEx extends RynoCheckExecutor {

	private static final String PERMISSION = RynoCheckPermissions.WALLET;

	private final WalletSessionDao walletSessionDao;
	private final MoneyAccountDao moneyAccDao;

	@Inject
	public WalletLoginCmEx(CommandContext commandContext,
						   WalletSessionDao walletSessionDao,
						   MoneyAccountDao moneyAccDao,
						   Plugin plugin) {
		super(commandContext, plugin, "wallet-login");
		this.moneyAccDao = moneyAccDao;
		this.walletSessionDao = walletSessionDao;
	}

	@Override
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		if (!(sender instanceof Player)) {
			// todo
			return true;
		}

		val uniqueId = ((Player) sender).getUniqueId();
		val pa = sender.addAttachment(getPlugin());

		if (args.length == 0) {
			if (pa.getPermissible().hasPermission(PERMISSION)) {
				if (walletSessionDao.isSessionExist(uniqueId)) {
					String username = walletSessionDao.getSessionName(uniqueId);
					sender.sendMessage("Your wallet: " + username);
					return true;
				}
			}
			return false;
		}

		if (args.length < 2) {
			sender.sendMessage("Username and password required");
			return false;
		}

		if (!moneyAccDao.authenticate(args[0], args[1])) {
			sender.sendMessage("Invalid username or password");
			return true;
		}

		if (pa.getPermissible().hasPermission(PERMISSION)) {
			sender.sendMessage("You are already login");
			return true;
		}

		pa.setPermission(PERMISSION, true);
		walletSessionDao.addSession(args[0], uniqueId);
		sender.sendMessage("Welcome to your private wallet " + args[0]);

		return true;
	}
}
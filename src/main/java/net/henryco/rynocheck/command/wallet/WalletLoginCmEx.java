package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Provide;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.val;
import net.henryco.rynocheck.account.MoneyAccountService;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.session.SessionDao;
import net.henryco.rynocheck.permission.RynoCheckPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Henry on 15/01/18.
 */
@Provide @Singleton
public class WalletLoginCmEx extends RynoCheckExecutor {

	private static final String PERMISSION = RynoCheckPermissions.WALLET;

	private final MoneyAccountService moneyAccountService;
	private final SessionDao walletSessionDao;

	@Inject
	public WalletLoginCmEx(
			MoneyAccountService moneyAccountService,
			CommandContext commandContext,
			SessionDao walletSessionDao,
			Plugin plugin
	) {
		super(commandContext, plugin, 2, "wallet-login");
		this.moneyAccountService = moneyAccountService;
		this.walletSessionDao = walletSessionDao;
	}

	@Override
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return true; // todo
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

		if (args[1] == null) {
			sender.sendMessage("Username and password required");
			return false;
		}

		if (!moneyAccountService.authenticate(args[0], args[1])) {
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
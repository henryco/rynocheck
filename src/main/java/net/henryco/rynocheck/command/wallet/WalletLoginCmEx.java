package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.MoneyAccountDao;
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

	private final MoneyAccountDao moneyAccDao;

	@Inject
	public WalletLoginCmEx(CommandContext commandContext,
						   MoneyAccountDao moneyAccDao,
						   Plugin plugin) {
		super(commandContext, plugin);
		this.moneyAccDao = moneyAccDao;
	}

	@Override // <wallet-login> {name} {password}
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		if (args.length == 0) return false;

		if (args.length != 2) {
			sender.sendMessage("Username and password required");
			return false;
		}

		if (!moneyAccDao.authenticate(args[0], args[1])) {
			sender.sendMessage("Invalid username or password");
			return false;
		}

		if (!(sender instanceof Player)) {
			// todo
			return true;
		}

		val permission = RynoCheckPermissions.WALLET_LOGIN;
		val pa = sender.addAttachment(getPlugin());

		if (pa.getPermissible().hasPermission(permission)) {
			sender.sendMessage("You are already login");
			return true;
		}

		pa.setPermission(permission, true);



		return true;
	}
}
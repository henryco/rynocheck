package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.session.SessionDao;
import net.henryco.rynocheck.permission.RynoCheckPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

import static net.henryco.rynocheck.permission.RynoCheckPermissions.WALLET;

/**
 * @author Henry on 15/01/18.
 */
@Component @Singleton
public class WalletLogoutCmEx extends RynoCheckExecutor {

	private final SessionDao walletSessionDao;

	@Inject
	public WalletLogoutCmEx(SessionDao walletSessionDao,
							CommandContext commandContext,
							Plugin plugin) {
		super(commandContext, plugin, "wallet-logout");
		this.walletSessionDao = walletSessionDao;
	}

	@Override
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		if (!(sender instanceof Player)) {
			return true; // todo
		}

		UUID uuid = ((Player) sender).getUniqueId();
		if (walletSessionDao.isSessionExist(uuid)) {
			walletSessionDao.removeSession(uuid);
			RynoCheckPermissions.Utils.unsetPermission(sender, WALLET);
			sender.sendMessage("Done!");
		} else {
			sender.sendMessage("You should login first!");
		}

		return true;
	}
}
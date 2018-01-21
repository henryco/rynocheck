package net.henryco.rynocheck.command.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.command.RynoCheckExecutor;
import net.henryco.rynocheck.context.CommandContext;
import net.henryco.rynocheck.data.dao.session.WalletSessionDao;
import net.henryco.rynocheck.permission.RynoCheckPermissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * @author Henry on 15/01/18.
 */
@Component @Singleton
public class WalletLogoutCmEx extends RynoCheckExecutor {

	private final WalletSessionDao walletSessionDao;

	@Inject
	public WalletLogoutCmEx(WalletSessionDao walletSessionDao,
							CommandContext commandContext,
							Plugin plugin) {
		super(commandContext, plugin);
		this.walletSessionDao = walletSessionDao;
	}

	@Override
	public boolean onCommandExecute(CommandSender sender, Command command, String label, String[] args) throws Exception {

		if (sender instanceof Player) {
			UUID uuid = ((Player) sender).getUniqueId();
			if (walletSessionDao.isSessionExist(uuid)) {
				walletSessionDao.removeSession(uuid);

				val pa = sender.addAttachment(getPlugin());
				pa.unsetPermission(RynoCheckPermissions.WALLET);

				sender.sendMessage("Done!");
			} else sender.sendMessage("You should login first!");

			return true;
		}
		return false;
	}
}
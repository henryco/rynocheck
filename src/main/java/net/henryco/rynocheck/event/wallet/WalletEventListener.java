package net.henryco.rynocheck.event.wallet;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import lombok.val;
import net.henryco.rynocheck.data.dao.session.WalletSessionDao;
import net.henryco.rynocheck.event.RynoCheckEventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

@Component @Singleton
public class WalletEventListener extends RynoCheckEventListener {

	private final WalletSessionDao walletSessionDao;

	@Inject
	public WalletEventListener(WalletSessionDao walletSessionDao,
							   Plugin plugin) {
		super(plugin);
		this.walletSessionDao = walletSessionDao;
	}


	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		// TODO
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent event) {

		val uuid = event.getPlayer().getUniqueId();
		val name = walletSessionDao.getSessionName(uuid);
		walletSessionDao.removeSession(uuid);

		if (name != null)
			getLogger().info("Wallet session closed: " + name);
	}

}
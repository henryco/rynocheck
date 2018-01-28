package net.henryco.rynocheck.command.sub.currency;

import com.github.henryco.injector.meta.annotations.Component;
import com.github.henryco.injector.meta.annotations.Inject;
import com.github.henryco.injector.meta.annotations.Singleton;
import net.henryco.rynocheck.command.sub.RynoCheckSubCommand;
import net.henryco.rynocheck.data.dao.DaoBundle;
import org.bukkit.command.CommandSender;

@Component("SCListCurr") @Singleton
public class CurrencyListSubCommand implements RynoCheckSubCommand {

	private final DaoBundle daoBundle;

	@Inject
	public CurrencyListSubCommand(DaoBundle daoBundle) {
		this.daoBundle = daoBundle;
	}

	@Override
	public boolean executeSubCommand(CommandSender sender, String[] args) {
		return false;
	}
}
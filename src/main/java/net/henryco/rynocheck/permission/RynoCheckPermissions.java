package net.henryco.rynocheck.permission;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;

/**
 * @author Henry on 12/01/18.
 */
public interface RynoCheckPermissions  {

	Permission NAP = new Permission(
			"nap_member",
			PermissionDefault.FALSE,
			new HashMap<String, Boolean>() {{
				this.put("revenge", true);
				this.put("private_property", true);
			}}
	);


	Permission WALLET_LOGIN = new Permission(
			"wallet_login",
			PermissionDefault.FALSE
	);


	Permission WALLET_LOGOUT = new Permission(
			"wallet_logout",
			PermissionDefault.TRUE
	);
}
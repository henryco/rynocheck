package net.henryco.rynocheck.permission;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;

/**
 * @author Henry on 12/01/18.
 */
public interface RynoCheckPermissions  {

	String NAP = "nap_member";
	String WALLET = "wallet_login";


	final class Factory {

		public static Permission nap() {
			return new Permission(NAP, PermissionDefault.FALSE,
					new HashMap<String, Boolean>() {{
						this.put("revenge", true);
						this.put("private_property", true);
					}}
			);
		}

		public static Permission wallet(String username) {
			return new Permission(WALLET, username, PermissionDefault.FALSE);
		}
	}


}
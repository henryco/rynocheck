package net.henryco.rynocheck.permission;

import org.bukkit.permissions.*;

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


	final class Utils {

		public static void unsetPermission(Permissible permissible, String permission) {

			for (PermissionAttachmentInfo info : permissible.getEffectivePermissions()) {

				PermissionAttachment attachment = info.getAttachment();
				if (attachment == null)
					continue;

				Permissible per = attachment.getPermissible();
				if (per == null)
					continue;

				if (per.hasPermission(permission) || per.isPermissionSet(permission)) {
					attachment.unsetPermission(permission);
					permissible.recalculatePermissions();
				}

				unsetPermission(per, permission);
			}
		}
	}

}
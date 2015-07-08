package net.eithon.library.permissions;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.Bukkit;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

public class PermissionMisc {
	public static ZPermissionsService connectToPermissionService(EithonPlugin plugin) {
		try {
			return Bukkit.getServicesManager().load(ZPermissionsService.class);
			// this.oracleService = Bukkit.getServicesManager().load(Oracle.class); 
		}
		catch (NoClassDefFoundError e) {
			// Eh...
		}
		plugin.getEithonLogger().warning("The plugin %s will not work properly without the zPermissions plugin", plugin.getName());
		return null;
	}
}

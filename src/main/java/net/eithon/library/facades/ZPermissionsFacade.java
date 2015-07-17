package net.eithon.library.facades;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.plugin.eithonlibrary.Config;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

public class ZPermissionsFacade {
	private static ZPermissionsService zPermissionsService;
	
	static {
		zPermissionsService = Bukkit.getServicesManager().load(ZPermissionsService.class);
	}
	
	@Deprecated
	public static ZPermissionsService connectToPermissionService(EithonPlugin plugin) {
		if (zPermissionsService != null) return zPermissionsService;
		try {
			zPermissionsService = Bukkit.getServicesManager().load(ZPermissionsService.class);
			return zPermissionsService;
			// this.oracleService = Bukkit.getServicesManager().load(Oracle.class); 
		}
		catch (NoClassDefFoundError e) {
			// Eh...
		}
		plugin.getEithonLogger().warning("The plugin %s will not work properly without the zPermissions plugin", plugin.getName());
		return null;
	}

	public static void addPermissionGroup(Player player, String groupName) {
		Config.C.addGroupCommand.execute(player.getName(), groupName);
	}

	public static void removePermissionGroup(Player player, String groupName) {
		Config.C.removeGroupCommand.execute(player.getName(), groupName);
	}

	public static String[] getPlayerPermissionGroups(Player player) {
		return zPermissionsService.getPlayerGroups(player.getUniqueId()).toArray(new String[]{});
	}
	
	public static boolean hasPermissionGroup(Player player, String groupName) {
		return contains(getPlayerPermissionGroups(player), groupName);
	}

	private static boolean contains(String[] playerGroups, String searchFor)
	{
		for (String string : playerGroups) {
			if (string.equalsIgnoreCase(searchFor)) return true;
		}
		return false;
	}
}

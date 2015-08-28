package net.eithon.library.facades;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.plugin.eithonlibrary.Config;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

public class ZPermissionsFacade {
	private static ZPermissionsService zPermissionsService;
	
	public static void initialize(EithonPlugin plugin) {
		try {
			zPermissionsService = Bukkit.getServicesManager().load(ZPermissionsService.class);
		}
		catch (NoClassDefFoundError e) {
			plugin.getEithonLogger().warning("EithonLibrary could not connect to the zPermissions plugin when enabling the %s plugin.", plugin.getName());
		}
	}
	
	public static boolean isConnected() {
		return zPermissionsService != null;
	}

	public static void addPermissionGroup(Player player, String groupName) {
		if (!isConnectedOrError()) return;
		Config.C.addGroupCommand.execute(player.getName(), groupName);
	}

	public static void removePermissionGroup(Player player, String groupName) {
		if (!isConnectedOrError()) return;
		Config.C.removeGroupCommand.execute(player.getName(), groupName);
	}

	public static String[] getPlayerPermissionGroups(Player player) {
		if (!isConnectedOrError()) return new String[0];
		return zPermissionsService.getPlayerGroups(player.getUniqueId()).toArray(new String[0]);
	}
	
	public static boolean hasPermissionGroup(Player player, String groupName) {
		if (!isConnectedOrError()) return false;
		return contains(getPlayerPermissionGroups(player), groupName);
	}

	private static boolean isConnectedOrError() {
		if (isConnected()) return true;
		Bukkit.getLogger().warning("EithonLibrary is not connected to the zPermissions plugin. ZPermissionsFacade fails.");
		return false;
	}

	private static boolean contains(String[] playerGroups, String searchFor)
	{
		for (String string : playerGroups) {
			if (string.equalsIgnoreCase(searchFor)) return true;
		}
		return false;
	}
}

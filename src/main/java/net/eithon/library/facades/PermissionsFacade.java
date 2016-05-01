package net.eithon.library.facades;

import java.util.List;
import java.util.stream.Collectors;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.cheesesoftware.PowerfulPermsAPI.PermissionManager;
import com.github.cheesesoftware.PowerfulPermsAPI.PermissionPlayer;
import com.github.cheesesoftware.PowerfulPermsAPI.PowerfulPermsPlugin;
import com.github.cheesesoftware.PowerfulPermsAPI.ResponseRunnable;

public class PermissionsFacade {
	private static PermissionManager permissionManager;
	private static EithonPlugin eithonPlugin;

	public static void initialize(EithonPlugin plugin) {
		eithonPlugin = plugin;
		try {
			PowerfulPermsPlugin permissionPlugin = (PowerfulPermsPlugin) Bukkit.getPluginManager().getPlugin("PowerfulPerms");
			permissionManager = permissionPlugin.getPermissionManager();
		}
		catch (NoClassDefFoundError e) {
			plugin.getEithonLogger().warning("EithonLibrary could not connect to the PowerfulPerms plugin when enabling the %s plugin.", plugin.getName());
		}
	}

	public static boolean isConnected() {
		return permissionManager != null;
	}

	public static void addPermissionGroup(Player player, String groupName) {
		if (!isConnectedOrError()) return;
		verbose("addPermissionGroup", "Adding player %s to group %s", player.getName(), groupName);
		permissionManager.addPlayerGroup(player.getUniqueId(), groupName, new ResponseRunnable() {
			@Override
			public void run() {
				verbose("addPermissionGroup", "Response");
			}
		});
	}

	public static void removePermissionGroup(Player player, String groupName) {
		if (!isConnectedOrError()) return;
		verbose("removePermissionGroup", "Removeing player %s from group %s", player.getName(), groupName);
		permissionManager.removePlayerGroup(player.getUniqueId(), groupName, new ResponseRunnable() {
			@Override
			public void run() {
				verbose("removePermissionGroup", "Response");
			}
		});
	}

	public static String[] getPlayerPermissionGroups(Player player) {
		if (!isConnectedOrError()) return new String[0];
		PermissionPlayer permissionPlayer = permissionManager.getPermissionPlayer(player.getUniqueId());
		List<String> groupNames = permissionPlayer
				.getGroups()
				.stream()
				.map(group -> group.getName())
				.collect(Collectors.toList());

		verbose("getPlayerPermissionGroups", "Groups for player %s: %s", 
				player.getName(), String.join(", ", groupNames));
		return groupNames
				.toArray(new String[0]);
	}

	public static boolean hasPermissionGroup(Player player, String groupName) {
		if (!isConnectedOrError()) return false;
		return contains(getPlayerPermissionGroups(player), groupName);
	}

	private static boolean isConnectedOrError() {
		if (isConnected()) return true;
		Bukkit.getLogger().warning("EithonLibrary is not connected to the PowerPerms plugin. PermissionsFacade fails.");
		return false;
	}

	private static boolean contains(String[] playerGroups, String searchFor)
	{
		for (String string : playerGroups) {
			if (string.equalsIgnoreCase(searchFor)) return true;
		}
		return false;
	}

	static void verbose(String method, String format, Object... args) {
		String message = String.format(format, args);
		eithonPlugin.getEithonLogger().debug(DebugPrintLevel.MAJOR, "%s: %s", method, message);
	}
}

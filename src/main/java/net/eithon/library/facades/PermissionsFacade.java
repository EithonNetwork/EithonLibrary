package net.eithon.library.facades;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.github.cheesesoftware.PowerfulPermsAPI.PermissionManager;
import com.github.cheesesoftware.PowerfulPermsAPI.PermissionPlayer;
import com.github.cheesesoftware.PowerfulPermsAPI.PowerfulPermsPlugin;
import com.github.cheesesoftware.PowerfulPermsAPI.ResponseRunnable;

public class PermissionsFacade {
	private static PermissionManager permissionManager;
	private static EithonPlugin eithonPlugin;
	private static ResponseRunnable emptyResponseRunnable;

	public static void initialize(EithonPlugin plugin) {
		eithonPlugin = plugin;
		try {
			PowerfulPermsPlugin permissionPlugin = (PowerfulPermsPlugin) Bukkit.getPluginManager().getPlugin("PowerfulPerms");
			permissionManager = permissionPlugin.getPermissionManager();
		}
		catch (NoClassDefFoundError e) {
			plugin.getEithonLogger().warning("EithonLibrary could not connect to the PowerfulPerms plugin when enabling the %s plugin.", plugin.getName());
		}
		emptyResponseRunnable = new ResponseRunnable() {
			public void run() {}
		};
	}

	public static boolean isConnected() {
		return permissionManager != null;
	}

	public static void addPermissionGroup(OfflinePlayer player, String groupName) {
		if (!isConnectedOrError()) return;
		verbose("addPermissionGroup", "Adding player %s to group %s", player.getName(), groupName);
		permissionManager.addPlayerGroup(player.getUniqueId(), groupName, new ResponseRunnable() {
			@Override
			public void run() {
				verbose("addPermissionGroup", "Response");
			}
		});
	}

	public static void removePermissionGroup(OfflinePlayer player, String groupName) {
		if (!isConnectedOrError()) return;
		verbose("removePermissionGroup", "Removing player %s from group %s", player.getName(), groupName);
		permissionManager.removePlayerGroup(player.getUniqueId(), groupName, new ResponseRunnable() {
			@Override
			public void run() {
				verbose("removePermissionGroup", "Response");
			}
		});
	}

	public static String[] getPlayerPermissionGroups(OfflinePlayer player) {
		return getPlayerPermissionGroups(player.getUniqueId());
	}

	public static String[] getPlayerPermissionGroups(UUID playerId) {
		if (!isConnectedOrError()) return new String[0];
		PermissionPlayer permissionPlayer = permissionManager.getPermissionPlayer(playerId);
		if (permissionPlayer == null) return new String[0];
		List<String> groupNames = permissionPlayer
				.getGroups()
				.stream()
				.map(group -> group.getName())
				.collect(Collectors.toList());
		return groupNames
				.toArray(new String[0]);
	}

	public static void addPlayerPermissionAsync(final OfflinePlayer player, final String permission) {
		addPlayerPermissionAsync(player.getUniqueId(), player.getName(), permission, emptyResponseRunnable);
	}

	public static void removePlayerPermissionAsync(final OfflinePlayer player, final String permission) {
		removePlayerPermissionAsync(player.getUniqueId(), player.getName(), permission, emptyResponseRunnable);
	}

	public static void addPlayerPermissionAsync(
			final UUID playerId, 
			final String playerName, 
			final String permission, 
			final ResponseRunnable response) {
		if (!isConnectedOrError()) return;
		eithonPlugin.getEithonLogger().info("PermissionsFacade: Adding permission %s to player %s", permission, playerName);
		permissionManager.addPlayerPermission(playerId, playerName, permission, response);
	}

	public static void removePlayerPermissionAsync(
			final UUID playerId, 
			final String playerName,
			final String permission, 
			final ResponseRunnable response) {
		if (!isConnectedOrError()) return;
		eithonPlugin.getEithonLogger().info("PermissionsFacade: Removing permission %s from player %s", permission, playerName);
		permissionManager.removePlayerPermission(playerId, permission, response);
	}

	public static boolean hasPermissionGroup(OfflinePlayer player, String groupName) {
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

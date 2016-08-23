package net.eithon.library.facades;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.github.cheesesoftware.PowerfulPermsAPI.Group;
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
			plugin.logWarn("EithonLibrary could not connect to the PowerfulPerms plugin when enabling the %s plugin.", plugin.getName());
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
		Group group = permissionManager.getGroup(groupName);
		addPermissionGroup(player, group);
	}

	public static void addPermissionGroup(OfflinePlayer player, Group group) {
		if (!isConnectedOrError()) return;
		verbose("addPermissionGroup", "Adding player %s to group %s", player.getName(), group.getName());
		permissionManager.addPlayerGroup(player.getUniqueId(), group.getId(), new ResponseRunnable() {
			@Override
			public void run() {
				verbose("addPermissionGroup", this.getResponse());
			}
		});
	}

	public static void removePermissionGroup(OfflinePlayer player, Group group) {
		if (!isConnectedOrError()) return;
		verbose("removePermissionGroup", "Removing player %s from group %s", player.getName(), group);
		permissionManager.removePlayerGroup(player.getUniqueId(), group.getId(), new ResponseRunnable() {
			@Override
			public void run() {
				verbose("removePermissionGroup", this.getResponse());
			}
		});
	}

	public static String[] getPlayerPermissionGroupNames(OfflinePlayer player) {
		return getPlayerPermissionGroupNames(player.getUniqueId());
	}

	public static String[] getPlayerPermissionGroupNames(UUID playerId) {
		List<String> groupNames = getPlayerPermissionGroups(playerId)
				.values()
				.stream()
				.map(group -> group.getName())
				.collect(Collectors.toList());
		return groupNames
				.toArray(new String[0]);
	}

	public static HashMap<Integer, Group> getPlayerPermissionGroups(UUID playerId) {
		HashMap<Integer, Group> result = new HashMap<Integer, Group>();
		if (!isConnectedOrError()) return result;
		PermissionPlayer permissionPlayer = permissionManager.getPermissionPlayer(playerId);
		if (permissionPlayer == null) return result;
		for (Group group : permissionPlayer.getGroups()) {
			result.put(group.getId(), group);
		}
		return result;
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
		eithonPlugin.logInfo("PermissionsFacade: Adding permission %s to player %s", permission, playerName);
		permissionManager.addPlayerPermission(playerId, playerName, permission, response);
	}

	public static void removePlayerPermissionAsync(
			final UUID playerId, 
			final String playerName,
			final String permission, 
			final ResponseRunnable response) {
		if (!isConnectedOrError()) return;
		eithonPlugin.logInfo("PermissionsFacade: Removing permission %s from player %s", permission, playerName);
		permissionManager.removePlayerPermission(playerId, permission, response);
	}

	public static boolean hasPermissionGroup(OfflinePlayer player, String groupName) {
		if (!isConnectedOrError()) return false;
		return contains(getPlayerPermissionGroupNames(player), groupName);
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
		eithonPlugin.dbgMajor( "%s: %s", method, message);
	}

	public static Group getGroup(String name) {
		return permissionManager.getGroup(name);
	}
}

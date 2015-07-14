package net.eithon.library.permissions;

import java.util.Set;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.plugin.eithonlibrary.Config;

import org.bukkit.entity.Player;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

public class PermissionGroupLadder {
	private EithonPlugin _eithonPlugin;
	private boolean _isAccumulative;
	private String[] _permissionGroups;
	private ZPermissionsService _permissionService = null;

	public PermissionGroupLadder(
			EithonPlugin eithonPlugin, 
			boolean isAccumulative, 
			String[] permissionGroups) {
		this._eithonPlugin = eithonPlugin;
		this._permissionService = PermissionMisc.connectToPermissionService(eithonPlugin);
		this._isAccumulative = isAccumulative;
		this._permissionGroups = permissionGroups;
	}

	public boolean canUpdatePermissionGroups() { return this._permissionService != null; }

	public int getLevel(String permissionName) {
		for (int i = 0; i < this._permissionGroups.length; i++) {
			if (this._permissionGroups[i].equalsIgnoreCase(permissionName)) return i;
		}
		return -1;
	}

	public String getPermissionGroup(int levelStartAtOne) {
		if (levelStartAtOne < 1) return null;
		if (levelStartAtOne > this._permissionGroups.length) return null;
		return this._permissionGroups[levelStartAtOne-1];
	}

	public void updatePermissionGroups(Player player, int levelStartAtOne) {
		verbose("updatePermissionGroups", "Enter player = %s, index = %d", player.getName(), levelStartAtOne);
		Set<String> playerGroups = getPlayerPermissionGroups(player);
		verbose("updatePermissionGroups", "playerGroups: %s", String.join(", ", playerGroups));
		if (this._isAccumulative) addLowerLevels(player, levelStartAtOne, playerGroups);
		verbose("updatePermissionGroups", "Add group for level %d", levelStartAtOne);
		maybeAddGroup(player, levelStartAtOne, playerGroups);
		if (!this._isAccumulative) removeLowerLevels(player, levelStartAtOne, playerGroups);
		removeHigherLevels(player, levelStartAtOne, playerGroups);
		verbose("updatePermissionGroups", "Leave");
	}

	private void removeHigherLevels(Player player, int levelStartAtOne,
			Set<String> playerGroups) {
		if (levelStartAtOne < this._permissionGroups.length) {
			verbose("updatePermissionGroups", "Remove groups: %d-%d", levelStartAtOne+1, this._permissionGroups.length);
			for (int i = levelStartAtOne+1; i <= this._permissionGroups.length; i++) {
				maybeRemoveGroup(player, i, playerGroups);
			}
		}
	}

	private void addLowerLevels(Player player, int levelStartAtOne,
			Set<String> playerGroups) {
		if (levelStartAtOne <= 1) return;
		verbose("updatePermissionGroups", "Add groups: %d-%d", 1, levelStartAtOne-1);
		for (int i = 1; i < levelStartAtOne; i++) {
			maybeAddGroup(player, i, playerGroups);
		}		
	}

	private void removeLowerLevels(Player player, int levelStartAtOne,
			Set<String> playerGroups) {
		if (levelStartAtOne <= 1) return;
		verbose("updatePermissionGroups", "Remove groups: %d-%d", 1, levelStartAtOne-1);
		for (int i = 1; i < levelStartAtOne; i++) {
			maybeRemoveGroup(player, i, playerGroups);
		}		
	}

	public void reset(Player player) {
		updatePermissionGroups(player, 0);
	}

	private void maybeAddGroup(Player player, int levelStartAtOne, Set<String> playerGroups) {
		String levelGroup = getPermissionGroup(levelStartAtOne);
		if (!contains(playerGroups, levelGroup)) {
			verbose("maybeAddGroup", "Group %s not found for player %s, so we will add it.", levelGroup, player.getName());
			Config.C.addGroupCommand.execute(player.getName(), getPermissionGroup(levelStartAtOne));
		}
	}

	private void maybeRemoveGroup(Player player, int level, Set<String> playerGroups) {
		String levelGroup = getPermissionGroup(level);
		if (contains(playerGroups, levelGroup)) {
			verbose("maybeRemoveGroup", "Group %s found for player %s, so we will remove it.", levelGroup, player.getName());
			Config.C.removeGroupCommand.execute(player.getName(), getPermissionGroup(level));
		}
	}

	public int currentLevel(Player player) {
		if (this._permissionService == null) return -1;
		Set<String> currentGroups = getPlayerPermissionGroups(player);
		int levelStartAtOne = 0;
		if ((currentGroups != null) && (currentGroups.size() > 0)) {
			for (int i = 1; i <= this._permissionGroups.length; i++) {
				String groupName = getPermissionGroup(i);
				if (contains(currentGroups, groupName)) {
					levelStartAtOne = i;	
				}
			}
		}
		return levelStartAtOne;
	}

	private Set<String> getPlayerPermissionGroups(Player player) {
		return this._permissionService.getPlayerGroups(player.getUniqueId());
	}

	private boolean contains(Set<String> setOfStrings, String searchFor)
	{
		for (String string : setOfStrings) {
			if (string.equalsIgnoreCase(searchFor)) return true;
		}
		return false;
	}

	private void verbose(String method, String format, Object... args) {
		String message = String.format(format, args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "%s: %s", method, message);
	}
}

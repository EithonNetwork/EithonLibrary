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
		if (this._isAccumulative && (levelStartAtOne > 1)) {
			verbose("updatePermissionGroups", "Add groups: %d-%d", 1, levelStartAtOne);
			for (int i = 1; i < levelStartAtOne; i++) {
				maybeAddGroup(player, i, playerGroups);
			}		
		}	
		verbose("updatePermissionGroups", "Add group: %d", levelStartAtOne);
		maybeAddGroup(player, levelStartAtOne, playerGroups);
		if (!this._isAccumulative && (levelStartAtOne > 1)) {
			verbose("updatePermissionGroups", "Remove groups: %d-%d", 1, levelStartAtOne-1);
			for (int i = 1; i < levelStartAtOne; i++) {
				maybeRemoveGroup(player, i, playerGroups);
			}		
		}
		if (levelStartAtOne < this._permissionGroups.length) {
			verbose("updatePermissionGroups", "Remove groups: %d-%d", levelStartAtOne+1, this._permissionGroups.length);
			for (int i = levelStartAtOne+1; i <= this._permissionGroups.length; i++) {
				maybeRemoveGroup(player, i, playerGroups);
			}
		}
		verbose("updatePermissionGroups", "Leave");
	}

	public void reset(Player player) {
		updatePermissionGroups(player, 0);
	}

	private void maybeAddGroup(Player player, int levelStartAtOne, Set<String> playerGroups) {
		verbose("maybeAddGroup", "Enter for player %s, level %d", player.getName(), levelStartAtOne);
		String levelGroup = getPermissionGroup(levelStartAtOne);
		verbose("maybeAddGroup", "levelGroup: %s", levelGroup);
		if (!contains(playerGroups, levelGroup)) {
			verbose("maybeAddGroup", "Not found, so we will add the group %s for player %s", levelGroup, player.getName());
			Config.C.addGroupCommand.execute(player.getName(), getPermissionGroup(levelStartAtOne));
		}
		verbose("maybeAddGroup", "Leave");
	}

	private void maybeRemoveGroup(Player player, int level, Set<String> playerGroups) {
		verbose("maybeRemoveGroup", "Enter for player %s, level %d", player.getName(), level);
		String levelGroup = getPermissionGroup(level);
		verbose("maybeRemoveGroup", "levelGroup: %s", levelGroup);
		if (contains(playerGroups, levelGroup)) {
			verbose("maybeRemoveGroup", "Found, so we will remove the group %s for player %s", levelGroup, player.getName());
			Config.C.removeGroupCommand.execute(player.getName(), getPermissionGroup(level));
		}
		verbose("maybeRemoveGroup", "Leave");
	}

	public int currentLevel(Player player) {
		verbose("currentLevel", "Enter for player %s", player.getName());
		if (this._permissionService == null) return -1;
		Set<String> currentGroups = getPlayerPermissionGroups(player);
		int levelStartAtOne = 0;
		if ((currentGroups != null) && (currentGroups.size() > 0)) {
			verbose("currentLevel", "Current groups: %s", String.join(", ", currentGroups));
			for (int i = 1; i <= this._permissionGroups.length; i++) {
				String groupName = getPermissionGroup(i);
				verbose("currentLevel", "Check group: %s", groupName);
				if (contains(currentGroups, groupName)) {
					verbose("currentLevel", "Matches %s", groupName);
					levelStartAtOne = i;	
				}
			}
		}
		verbose("currentLevel", "Leave %d", levelStartAtOne);
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

package net.eithon.library.permissions;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.facades.ZPermissionsFacade;
import net.eithon.library.plugin.EithonLogger.DebugPrintLevel;

import org.bukkit.entity.Player;

public class PermissionGroupLadder {
	private EithonPlugin _eithonPlugin;
	private boolean _isAccumulative;
	private String[] _permissionGroups;

	public PermissionGroupLadder(
			EithonPlugin eithonPlugin, 
			boolean isAccumulative, 
			String[] permissionGroups) {
		this._eithonPlugin = eithonPlugin;
		this._isAccumulative = isAccumulative;
		this._permissionGroups = permissionGroups;
	}

	public boolean canUpdatePermissionGroups() { return ZPermissionsFacade.isConnected(); }

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

	public boolean updatePermissionGroups(Player player, int levelStartAtOne) {
		boolean anyChanged = false;
		boolean changed = false;
		verbose("updatePermissionGroups", "Enter player = %s, index = %d", player.getName(), levelStartAtOne);
		String[] playerGroups = ZPermissionsFacade.getPlayerPermissionGroups(player);
		verbose("updatePermissionGroups", "playerGroups: %s", String.join(", ", playerGroups));
		if (this._isAccumulative) changed = addLowerLevels(player, levelStartAtOne, playerGroups);
		anyChanged = anyChanged || changed;
		verbose("updatePermissionGroups", "Add group for level %d", levelStartAtOne);
		if (levelStartAtOne > 0) changed = maybeAddGroup(player, levelStartAtOne, playerGroups);
		anyChanged = anyChanged || changed;
		if (!this._isAccumulative) changed = removeLowerLevels(player, levelStartAtOne, playerGroups);
		anyChanged = anyChanged || changed;
		changed = removeHigherLevels(player, levelStartAtOne, playerGroups);
		anyChanged = anyChanged || changed;
		verbose("updatePermissionGroups", "Leave %s", anyChanged ? "TRUE" : "FALSE");
		return anyChanged;
	}

	private boolean removeHigherLevels(Player player, int levelStartAtOne,
			String[] playerGroups) {
		boolean anyChanged = false;
		if (levelStartAtOne < this._permissionGroups.length) {
			verbose("updatePermissionGroups", "Remove groups: %d-%d", levelStartAtOne+1, this._permissionGroups.length);
			for (int i = levelStartAtOne+1; i <= this._permissionGroups.length; i++) {
				boolean changed = maybeRemoveGroup(player, i, playerGroups);
				anyChanged = anyChanged || changed;
			}
		}
		return anyChanged;
	}

	private boolean addLowerLevels(Player player, int levelStartAtOne,
			String[] playerGroups) {
		if (levelStartAtOne <= 1) return false;
		boolean anyChanged = false;
		verbose("updatePermissionGroups", "Add groups: %d-%d", 1, levelStartAtOne-1);
		for (int i = 1; i < levelStartAtOne; i++) {
			boolean changed = maybeAddGroup(player, i, playerGroups);
			anyChanged = anyChanged || changed;
		}	
		return anyChanged;	
	}

	private boolean removeLowerLevels(Player player, int levelStartAtOne,
			String[] playerGroups) {
		if (levelStartAtOne <= 1) return false;
		boolean anyChanged = false;
		verbose("updatePermissionGroups", "Remove groups: %d-%d", 1, levelStartAtOne-1);
		for (int i = 1; i < levelStartAtOne; i++) {
			boolean changed = maybeRemoveGroup(player, i, playerGroups);
			anyChanged = anyChanged || changed;
		}
		return anyChanged;
	}

	public boolean reset(Player player) {
		return updatePermissionGroups(player, 0);
	}

	private boolean maybeAddGroup(Player player, int levelStartAtOne, String[] playerGroups) {
		String levelGroup = getPermissionGroup(levelStartAtOne);
		if (contains(playerGroups, levelGroup)) return false;
		verbose("maybeAddGroup", "Adding group %s for player %s.", levelGroup, player.getName());
		ZPermissionsFacade.addPermissionGroup(player, getPermissionGroup(levelStartAtOne));
		return true;
	}

	private boolean maybeRemoveGroup(Player player, int level, String[] playerGroups) {
		String levelGroup = getPermissionGroup(level);
		if (!contains(playerGroups, levelGroup)) return false;
		verbose("maybeRemoveGroup", "Removing group %s for player %s.", levelGroup, player.getName());
		ZPermissionsFacade.removePermissionGroup(player, getPermissionGroup(level));
		return true;
	}

	public int currentLevel(Player player) {
		if (!ZPermissionsFacade.isConnected()) return 0;
		String[] currentGroups = ZPermissionsFacade.getPlayerPermissionGroups(player);
		int levelStartAtOne = 0;
		if ((currentGroups != null) && (currentGroups.length > 0)) {
			for (int i = 1; i <= this._permissionGroups.length; i++) {
				String groupName = getPermissionGroup(i);
				if (contains(currentGroups, groupName)) {
					levelStartAtOne = i;	
				}
			}
		}
		return levelStartAtOne;
	}

	private boolean contains(String[] playerGroups, String searchFor)
	{
		for (String string : playerGroups) {
			if (string.equalsIgnoreCase(searchFor)) return true;
		}
		return false;
	}

	private void verbose(String method, String format, Object... args) {
		String message = String.format(format, args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "%s: %s", method, message);
	}
}

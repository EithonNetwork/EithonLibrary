package net.eithon.library.permissions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.facades.PermissionsFacade;

import org.bukkit.entity.Player;

import com.github.cheesesoftware.PowerfulPermsAPI.Group;

public class PermissionGroupLadder {
	private EithonPlugin _eithonPlugin;
	private boolean _isAccumulative;
	private Group[] _permissionGroups;

	public PermissionGroupLadder(
			EithonPlugin eithonPlugin, 
			boolean isAccumulative, 
			String[] permissionGroups) {
		this._eithonPlugin = eithonPlugin;
		this._isAccumulative = isAccumulative;
		this._permissionGroups = Arrays.asList(permissionGroups)
				.stream()
				.map(name -> PermissionsFacade.getGroup(name))
				.collect(Collectors.toList()).toArray(new Group[] {});
	}

	public boolean canUpdatePermissionGroups() { return PermissionsFacade.isConnected(); }

	public int getLevel(String permissionName) {
		for (int i = 0; i < this._permissionGroups.length; i++) {
			if (this._permissionGroups[i].getName().equalsIgnoreCase(permissionName)) return i;
		}
		return -1;
	}

	Group getPermissionGroup(int levelStartAtOne) {
		if (levelStartAtOne < 1) return null;
		if (levelStartAtOne > this._permissionGroups.length) return null;
		return this._permissionGroups[levelStartAtOne-1];
	}

	public String getPermissionGroupName(int levelStartAtOne) {
		Group group = getPermissionGroup(levelStartAtOne);
		if (group == null) return null;
		return group.getName();
	}

	public boolean updatePermissionGroups(Player player, int levelStartAtOne) {
		boolean anyChanged = false;
		boolean changed = false;
		verbose("updatePermissionGroups", "Enter player = %s, index = %d", player.getName(), levelStartAtOne);
		HashMap<Integer, Group> playerGroups = PermissionsFacade.getPlayerPermissionGroups(player.getUniqueId());
		verbose("updatePermissionGroups", "playerGroups: %s", String.join(", ", getGroupNames(playerGroups)));
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

	private List<String> getGroupNames(HashMap<Integer, Group> playerGroups) {
		return playerGroups.values()
		.stream()
		.map(group -> group.getName())
		.collect(Collectors.toList());
	}

	private boolean removeHigherLevels(Player player, int levelStartAtOne,
			HashMap<Integer, Group> playerGroups) {
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

	private boolean addLowerLevels(Player player, int levelStartAtOne, HashMap<Integer, Group> playerGroups) {
		if (levelStartAtOne <= 1) return false;
		boolean anyChanged = false;
		verbose("updatePermissionGroups", "Add groups: %d-%d", 1, levelStartAtOne-1);
		for (int i = 1; i < levelStartAtOne; i++) {
			boolean changed = maybeAddGroup(player, i, playerGroups);
			anyChanged = anyChanged || changed;
		}	
		return anyChanged;	
	}

	private boolean removeLowerLevels(Player player, int levelStartAtOne, HashMap<Integer, Group> playerGroups) {
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

	private boolean maybeAddGroup(Player player, int levelStartAtOne, HashMap<Integer, Group> playerGroups) {
		Group levelGroup = getPermissionGroup(levelStartAtOne);
		if (playerGroups.containsKey(levelGroup.getId())) return false;
		verbose("maybeAddGroup", "Adding group %s for player %s.", levelGroup.getName(), player.getName());
		PermissionsFacade.addPermissionGroup(player, levelGroup);
		return true;
	}

	private boolean maybeRemoveGroup(Player player, int level, HashMap<Integer, Group> playerGroups) {
		Group levelGroup = getPermissionGroup(level);
		if (!playerGroups.containsKey(levelGroup.getId())) return false;
		verbose("maybeRemoveGroup", "Removing group %s for player %s.", levelGroup.getName(), player.getName());
		PermissionsFacade.removePermissionGroup(player, levelGroup);
		return true;
	}

	public int currentLevel(Player player) {
		if (!PermissionsFacade.isConnected()) return 0;
		HashMap<Integer, Group> currentGroups = PermissionsFacade.getPlayerPermissionGroups(player.getUniqueId());
		int levelStartAtOne = 0;
		if ((currentGroups != null) && (currentGroups.size() > 0)) {
			for (int i = 1; i <= this._permissionGroups.length; i++) {
				Group group = getPermissionGroup(i);
				if (currentGroups.containsKey(group.getId())) {
					levelStartAtOne = i;	
				}
			}
		}
		return levelStartAtOne;
	}
	
	private void verbose(String method, String format, Object... args)
	{
		this._eithonPlugin.dbgVerbose("PermissionGroupLadder", method, format, args);
	}
}

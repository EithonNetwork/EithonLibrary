package net.eithon.library.move;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import net.eithon.library.core.PlayerCollection;
import net.eithon.library.plugin.Logger;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

public class MoveEventHandler {
	public static PlayerCollection<BlockMover> _playerSubscriptions = new PlayerCollection<BlockMover>();
	public static HashMap<String, IBlockMoverFollower> _generalSubscriptions = new HashMap<String, IBlockMoverFollower>();
	public static void handle(PlayerMoveEvent event) {
		for (IBlockMoverFollower follower : _generalSubscriptions.values()) {
			follower.moveEventHandler(event);
			if (event.isCancelled()) return;
		}
		BlockMover mover = _playerSubscriptions.get(event.getPlayer());
		if (mover == null) return;
		mover.informFollowers(event);
	}

	public static void addBlockMover(Player player, IBlockMoverFollower follower) {
		BlockMover mover = _playerSubscriptions.get(player);
		if (mover == null) {
			mover = new BlockMover(player);
			_playerSubscriptions.put(player, mover);
		}
		mover.addFollower(follower);
	}

	public static void removeBlockMover(Player player, IBlockMoverFollower follower) {
		BlockMover mover = _playerSubscriptions.get(player);
		if (mover == null) return;
		mover.removeFollower(follower);
		if (!mover.hasFollowers()) _playerSubscriptions.remove(player);
	}

	public static void addBlockMover(IBlockMoverFollower follower) {
		if (_generalSubscriptions.containsKey(follower.getName())) {
			Logger.libraryWarning(
					"Bad programming or duplicate follower names? Follower %s has obsolete calls to net.eithon.library.move.MoveEventHandler.addBlockMover().",
					follower.getName());
			return;
		}
		_generalSubscriptions.put(follower.getName(), follower);
	}

	public static void removeBlockMover(IBlockMoverFollower follower) {
		if (!_generalSubscriptions.containsKey(follower.getName())) return;
		_generalSubscriptions.remove(follower.getName());
	}
}

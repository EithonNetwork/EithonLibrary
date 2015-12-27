package net.eithon.library.move;

import net.eithon.library.core.PlayerCollection;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveEventHandler {
	private static PlayerCollection<BlockMover> _playerSubscriptions = new PlayerCollection<BlockMover>();
	
	public static void handle(PlayerMoveEvent event) {
		Location fromLocation = event.getFrom();
		Location toLocation = event.getTo();
		if (isSameBlock(fromLocation.getBlock(), toLocation.getBlock())) return;
		
		Player player = event.getPlayer();
		EithonPlayerMoveOneBlockEvent e = 
				new EithonPlayerMoveOneBlockEvent(player, fromLocation, toLocation);
		player.getServer().getPluginManager().callEvent(e);
		
		BlockMover mover = _playerSubscriptions.get(player);
		if (mover == null) return;
		mover.informFollowers(event);
	}

	// Use EithonPlayerMoveOneBlockEvent instead
	@Deprecated
	public static void addBlockMover(Player player, IBlockMoverFollower follower) {
		BlockMover mover = _playerSubscriptions.get(player);
		if (mover == null) {
			mover = new BlockMover(player);
			_playerSubscriptions.put(player, mover);
		}
		mover.addFollower(follower);
	}

	// Use EithonPlayerMoveOneBlockEvent instead
	@Deprecated
	public static void removeBlockMover(Player player, IBlockMoverFollower follower) {
		BlockMover mover = _playerSubscriptions.get(player);
		if (mover == null) return;
		mover.removeFollower(follower);
		if (!mover.hasFollowers()) _playerSubscriptions.remove(player);
	}
	
	private static boolean isSameBlock(Block from, Block to) {
		return (from.getX() == to.getX()) && (from.getZ() == to.getZ()) && (from.getY() == to.getY());
	}
}

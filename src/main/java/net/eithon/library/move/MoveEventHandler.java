package net.eithon.library.move;

import net.eithon.library.core.PlayerCollection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveEventHandler {
	private static PlayerCollection<BlockMover> _playerSubscriptions = new PlayerCollection<BlockMover>();
	
	public static void handle(PlayerMoveEvent event) {
		Location fromLocation = event.getFrom();
		Location toLocation = event.getTo();
		if (isSameHalfBlock(fromLocation, toLocation)) return;
		Player player = event.getPlayer();
		
		// Half block move
		EithonPlayerMoveHalfBlockEvent ehb = 
				new EithonPlayerMoveHalfBlockEvent(player, fromLocation, toLocation);
		player.getServer().getPluginManager().callEvent(ehb);
		
		if (!ehb.crossesBlockBoundary()) return;
		
		// One full block move
		EithonPlayerMoveOneBlockEvent eob = 
				new EithonPlayerMoveOneBlockEvent(player, fromLocation, toLocation);
		player.getServer().getPluginManager().callEvent(eob);
		
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
	
	private static boolean isSameHalfBlock(Location from, Location to) {
		return 
				sameHalf(from.getX(), to.getX()) &&
				sameHalf(from.getZ(), to.getZ()) &&
				sameHalf(from.getY(), to.getY());
	}
	
	private static boolean sameHalf(double firstDouble, double secondDouble) {
		long firstLong = (long) (firstDouble*2);
		long secondLong = (long) (secondDouble*2);
		return firstLong == secondLong;
	}
}

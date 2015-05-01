package net.eithon.library.move;



import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import net.eithon.library.core.PlayerCollection;

public class MoveEventHandler {
	public static PlayerCollection<BlockMover> _movers = new PlayerCollection<BlockMover>();
	public static void handle(PlayerMoveEvent event) {
		if (event.isCancelled()) return;
		if (isSameBlock(event.getFrom().getBlock(), event.getTo().getBlock())) return;
		BlockMover mover = _movers.get(event.getPlayer());
		if (mover == null) return;
		if (!mover.hasMoved(event.getTo())) return;
		mover.informFollowers(event);
	}
	
	private static boolean isSameBlock(Block from, Block to) {
		return (from.getX() == to.getX()) && (from.getY() == to.getY()) && (from.getZ() == to.getZ());
	}

	public static void addBlockMover(Player player, IBlockMoverFollower follower) {
		BlockMover mover = _movers.get(player);
		if (mover == null) {
			mover = new BlockMover(player);
			_movers.put(player, mover);
		}
		mover.addFollower(follower);
	}
	
	public static void removeBlockMover(Player player, IBlockMoverFollower follower) {
		BlockMover mover = _movers.get(player);
		if (mover == null) return;
		mover.removeFollower(follower);
		if (!mover.hasFollowers()) _movers.remove(player);
	}
}

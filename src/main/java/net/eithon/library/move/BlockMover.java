package net.eithon.library.move;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

@Deprecated
class BlockMover {
	private Block _lastBlock;
	private HashMap<String, IBlockMoverFollower> _followers;
	
	@Deprecated
	public BlockMover(Player player) {
		this._lastBlock = player.getLocation().getBlock();
		this._followers = new HashMap<String, IBlockMoverFollower>();
	}
	
	@Deprecated
	public boolean hasMoved(Location to) {
		Block currentBlock = to.getBlock();
		if (currentBlock.equals(this._lastBlock)) return false;
		this._lastBlock = currentBlock;
		return true;
	}

	@Deprecated
	public void addFollower(IBlockMoverFollower follower) {
		this._followers.put(follower.getName(), follower);
	}

	@Deprecated
	public void removeFollower(IBlockMoverFollower follower) {
		this._followers.remove(follower.getName());
	}

	@Deprecated
	public boolean hasFollowers() {
		return this._followers.size() > 0;
	}

	@Deprecated
	public void informFollowers(PlayerMoveEvent event) {
		for (IBlockMoverFollower follower : this._followers.values()) {
			follower.moveEventHandler(event);
			if (event.isCancelled()) return;
		}
	}
}

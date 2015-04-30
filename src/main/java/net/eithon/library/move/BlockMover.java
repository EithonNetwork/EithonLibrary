package net.eithon.library.move;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class BlockMover {
	private Block _lastBlock;
	private HashMap<String, IBlockMoverFollower> _followers;
	
	public BlockMover(Player player) {
		this._lastBlock = player.getLocation().getBlock();
		this._followers = new HashMap<String, IBlockMoverFollower>();
	}
	
	public boolean hasMoved(Location to) {
		Block currentBlock = to.getBlock();
		if (currentBlock.equals(this._lastBlock)) return false;
		this._lastBlock = currentBlock;
		return true;
	}

	public void addFollower(IBlockMoverFollower follower) {
		this._followers.put(follower.getName(), follower);
	}

	public void removeFollower(IBlockMoverFollower follower) {
		this._followers.remove(follower.getName());
	}

	public boolean hasFollowers() {
		return this._followers.size() > 0;
	}

	public void informFollowers(PlayerMoveEvent event) {
		for (IBlockMoverFollower follower : this._followers.values()) {
			follower.moveEventHandler(event);
			if (event.isCancelled()) return;
		}
	}
}

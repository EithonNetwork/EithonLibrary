package net.eithon.library.move;

import net.eithon.library.extensions.EithonBlock;
import net.eithon.library.extensions.EithonPlayer;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EithonPlayerMoveOneBlockEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private EithonPlayer _player;
	private EithonBlock _fromBlock;
	private EithonBlock _toBlock;

	public EithonPlayerMoveOneBlockEvent(Player player, Block fromBlock, Block toBlock) {
		this._player = new EithonPlayer(player);
		this._fromBlock = new EithonBlock(fromBlock);
		this._toBlock = new EithonBlock(toBlock);
	}

	public static HandlerList getHandlerList() {
		return handlers;
		}


	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public Player getPlayer() { return this._player.getPlayer(); }
	
	public Block getFromBlock() { return this._fromBlock.getBlock(); }
	
	public Block getToBlock() { return this._toBlock.getBlock(); }
}

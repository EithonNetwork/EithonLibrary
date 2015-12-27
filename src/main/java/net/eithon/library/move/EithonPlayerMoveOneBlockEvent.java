package net.eithon.library.move;

import net.eithon.library.extensions.EithonLocation;
import net.eithon.library.extensions.EithonPlayer;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EithonPlayerMoveOneBlockEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private EithonPlayer _player;
	private EithonLocation _fromLocation;
	private EithonLocation _toLocation;

	public EithonPlayerMoveOneBlockEvent(Player player, Location fromLocation, Location toLocation) {
		this._player = new EithonPlayer(player);
		this._fromLocation = new EithonLocation(fromLocation);
		this._toLocation = new EithonLocation(toLocation);
	}

	public static HandlerList getHandlerList() {
		return handlers;
		}


	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public Player getPlayer() { return this._player.getPlayer(); }
	
	public Block getFromBlock() { return this._fromLocation.getLocation().getBlock(); }
	
	public Block getToBlock() { return this._toLocation.getLocation().getBlock(); }
	
	public Location getFromLocation() { return this._fromLocation.getLocation(); }
	
	public Location getToLocation() { return this._toLocation.getLocation(); }
}

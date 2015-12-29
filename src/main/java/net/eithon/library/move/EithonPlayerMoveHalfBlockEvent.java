package net.eithon.library.move;

import net.eithon.library.extensions.EithonLocation;
import net.eithon.library.extensions.EithonPlayer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EithonPlayerMoveHalfBlockEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private EithonPlayer _player;
	private EithonLocation _fromLocation;
	private EithonLocation _toLocation;
	private boolean _crossesBlockBoundary;

	public EithonPlayerMoveHalfBlockEvent(Player player, Location fromLocation, Location toLocation) {
		this._player = new EithonPlayer(player);
		this._fromLocation = new EithonLocation(fromLocation);
		this._toLocation = new EithonLocation(toLocation);
		this._crossesBlockBoundary = !fromLocation.getBlock().equals(toLocation.getBlock());
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}


	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public Player getPlayer() { return this._player.getPlayer(); }
	
	public Location getFromLocation() { return this._fromLocation.getLocation(); }
	
	public Location getToLocation() { return this._toLocation.getLocation(); }
	
	public boolean crossesBlockBoundary()  { return this._crossesBlockBoundary; }
}

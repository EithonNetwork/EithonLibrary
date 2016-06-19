package net.eithon.library.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EithonPublicMessageEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String _message;
	private Player _concerningPlayer;
	private boolean _useTitle;

	public EithonPublicMessageEvent(String message, boolean useTitle) {
		this._message = message;
		this._useTitle = useTitle;
	}

	public EithonPublicMessageEvent(Player player, String message, boolean useTitle) {
		this(message, useTitle);
		this._concerningPlayer = player;
	}
	
	public String getMessage() { return this._message; }
	public boolean getUseTitle() { return this._useTitle; }
	public Player getConcerningPlayer() { return this._concerningPlayer; }

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}

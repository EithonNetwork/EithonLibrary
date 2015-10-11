package net.eithon.library.bungee;

import net.eithon.library.extensions.EithonPlayer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EithonBungeeJoinEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private EithonPlayer _player;
	private String _mainGroup;
	private String _serverName;

	public EithonBungeeJoinEvent(String serverName, EithonPlayer player, String mainGroup) {
		this._serverName = serverName;
		this._player = player;
		this._mainGroup = mainGroup;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}


	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public EithonPlayer getPlayer() { return this._player; }

	public String getMainGroup() { return this._mainGroup; }

	public String getServerName() { return this._serverName; }
}

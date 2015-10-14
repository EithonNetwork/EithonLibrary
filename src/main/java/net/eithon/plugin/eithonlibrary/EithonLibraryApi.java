package net.eithon.plugin.eithonlibrary;

import net.eithon.library.bungee.BungeeController;

import org.bukkit.entity.Player;

public class EithonLibraryApi {
	private BungeeController _controller;

	EithonLibraryApi(BungeeController _bungeeController) {
		this._controller = _bungeeController;
	}
	
	public String getBungeeServerName() {
		return this._controller.getServerName();
	}

	public boolean bungeeBroadcastMessage(String message, boolean useTitle) {
		return this._controller.broadcastMessage(message, useTitle);
	}

	public boolean teleportPlayerToServer(Player player, String serverName) {
		return this._controller.connectToServer(player, serverName);
	}

	public void bungeeJoinEvent(Player player) {
		this._controller.joinEvent(player);
	}

	public void bungeeQuitEvent(Player player) {
		this._controller.quitEvent(player);
	}
}

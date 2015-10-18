package net.eithon.plugin.eithonlibrary;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.json.IJsonObject;

import org.bukkit.entity.Player;

public class EithonLibraryApi {
	private BungeeController _controller;

	EithonLibraryApi(BungeeController _bungeeController) {
		this._controller = _bungeeController;
	}
	
	public String getBungeeServerName() {
		return this._controller.getServerName();
	}
	
	public String getPrimaryBungeeServerName() {
		return Config.V.primaryBungeeServer;
	}
	
	public boolean isPrimaryBungeeServer(String bungeeServerName) {
		if (bungeeServerName == null) return false;
		String primaryBungeeServerName = getPrimaryBungeeServerName();
		if (primaryBungeeServerName == null) return false;
		return bungeeServerName.equalsIgnoreCase(primaryBungeeServerName);
	}
	
	public boolean isPrimaryBungeeServer() {
		String bungeeServerName = getBungeeServerName();
		if (bungeeServerName == null) return true;
		return isPrimaryBungeeServer(bungeeServerName);
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

	public boolean bungeeSendDataToServer(String serverName, String name, IJsonObject<?> data, boolean rejectOld) {
		return this._controller.sendEventToServer(serverName, name, data, rejectOld);
	}
}

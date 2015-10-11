package net.eithon.library.bungee;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {

	private BungeeController _controller;

	public EventListener(EithonPlugin eithonPlugin, BungeeController controller) {
		this._controller = controller;
	}

	// Inform everyone that we have a new player on the server
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		this._controller.eithonBungeeJoinEvent(player);
	}

	/*
	// Inform everyone that we a player has left the server
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		this._controller.eithonBungeeQuitEvent(player);
	}
	*/
}

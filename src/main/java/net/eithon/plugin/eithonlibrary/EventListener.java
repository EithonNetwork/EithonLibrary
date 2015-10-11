package net.eithon.plugin.eithonlibrary;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.move.MoveEventHandler;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

	private BungeeController _bungeeController;

	public EventListener(EithonPlugin eithonPlugin, BungeeController controller) {
		this._bungeeController = controller;
	}
	
	// Handle move by block
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (event.isCancelled()) return;
		if (isSameBlock(event.getFrom().getBlock(), event.getTo().getBlock())) return;
		MoveEventHandler.handle(event);
	}
	
	private static boolean isSameBlock(Block from, Block to) {
		return (from.getX() == to.getX()) && (from.getZ() == to.getZ()) && (from.getY() == to.getY());
	}

	// Inform everyone that we have a new player on the server
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		this._bungeeController.eithonBungeeJoinEvent(player);
	}

	// Inform everyone that a player has left the server
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		this._bungeeController.eithonBungeeQuitEvent(player);
	}
}

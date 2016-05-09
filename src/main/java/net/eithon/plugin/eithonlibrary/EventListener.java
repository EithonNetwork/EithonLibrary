package net.eithon.plugin.eithonlibrary;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.move.MoveEventHandler;
import net.eithon.library.time.TimeMisc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class EventListener implements Listener {

	private EithonPlugin _eithonPlugin;

	public EventListener(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
	}
	
	// Handle move by block
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (event.isCancelled()) return;
		MoveEventHandler.handle(event);
	}

	// Inform everyone that we have a new player on the server
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		delayedBungeeJoinEvent(player);
	}

	private void delayedBungeeJoinEvent(final Player player) {
		final EithonLibraryApi api = this._eithonPlugin.getApi();
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(this._eithonPlugin, new Runnable() {
			public void run() {
				api.bungeeJoinEvent(player);
			}
		}, TimeMisc.secondsToTicks(2));
	}

	// EithonBungeeFixes now takes care of informing about quitting players.
	
	// Inform everyone that a player has left the server
//	@EventHandler
//	public void onPlayerQuitEvent(PlayerQuitEvent event) {
//		Player player = event.getPlayer();
//		if (player == null) return;
//		this._eithonPlugin.getApi().bungeeQuitEvent(player);
//	}
}

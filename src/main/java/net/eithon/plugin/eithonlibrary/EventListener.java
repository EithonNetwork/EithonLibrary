package net.eithon.plugin.eithonlibrary;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.move.MoveEventHandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventListener implements Listener {

	public EventListener(EithonPlugin eithonPlugin) {
	}
	
	// Handle move by block
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (event.isCancelled()) return;
		MoveEventHandler.handle(event);
	}
}

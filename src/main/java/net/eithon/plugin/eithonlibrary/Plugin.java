package net.eithon.plugin.eithonlibrary;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.move.MoveEventHandler;
import net.eithon.library.plugin.GeneralMessage;
import net.eithon.library.plugin.Logger;

public final class Plugin extends EithonPlugin implements Listener {
	public static EithonPlugin eithonPlugin;
	
	@Override
	public void onEnable() {
		eithonPlugin = this;
		super.onEnable();
		Logger logger = getEithonLogger();
		Logger.setDefaultDebug(logger);
		GeneralMessage.initialize(this);
		super.activate(null, null);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
	
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		MoveEventHandler.handle(event);
	}
}

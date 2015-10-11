package net.eithon.plugin.eithonlibrary;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.bungee.BungeeSender;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.move.MoveEventHandler;
import net.eithon.library.plugin.Logger;
import net.eithon.library.time.AlarmTrigger;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public final class Plugin extends EithonPlugin implements Listener {
	public static EithonPlugin eithonPlugin;
	private BungeeController _bungeeController;
	
	@Override
	public void onEnable() {
		eithonPlugin = this;
		super.onEnable();
		Logger logger = getEithonLogger();
		Logger.setDefaultDebug(logger);
		Config.load(this);
		this._bungeeController = new BungeeController(this);
		super.activate(null, this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		AlarmTrigger.get().disable();
	}
	
	public BungeeSender getBungeeSender() {
		return this._bungeeController.getBungeeSender();
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
}

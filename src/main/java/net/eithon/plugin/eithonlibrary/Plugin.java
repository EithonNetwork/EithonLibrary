package net.eithon.plugin.eithonlibrary;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger;
import net.eithon.library.time.AlarmTrigger;

import org.bukkit.event.Listener;

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
		this._bungeeController.createBungeeListener();
		EventListener eventListener = new EventListener(eithonPlugin, this._bungeeController);
		super.activate(null, eventListener);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		AlarmTrigger.get().disable();
	}
}

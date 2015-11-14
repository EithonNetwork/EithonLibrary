package net.eithon.plugin.eithonlibrary;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.EithonLogger;
import net.eithon.library.time.AlarmTrigger;

import org.bukkit.event.Listener;

public final class EithonLibraryPlugin extends EithonPlugin implements Listener {
	private BungeeController _bungeeController;
	private EithonLibraryApi _api;
	
	@Override
	public void onEnable() {
		super.onEnable();
		EithonLogger logger = getEithonLogger();
		EithonLogger.setDefaultDebug(logger);
		Config.load(this);
		this._bungeeController = new BungeeController(this);
		EventListener eventListener = new EventListener(this);
		this._api = new EithonLibraryApi(this._bungeeController);
		super.activate(null, eventListener);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		AlarmTrigger.get().disable();
	}
	
	public EithonLibraryApi getApi() {return this._api; }
}

package net.eithon.plugin.eithonlibrary;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger;
import net.eithon.library.time.AlarmTrigger;

import org.bukkit.event.Listener;

public final class EithonLibraryPlugin extends EithonPlugin implements Listener {
	private BungeeController _bungeeController;
	private EithonLibraryApi _api;
	
	@Override
	public void onEnable() {
		super.onEnable();
		Logger logger = getEithonLogger();
		Logger.setDefaultDebug(logger);
		Config.load(this);
		this._bungeeController = new BungeeController(this);
		this._bungeeController.createBungeeListener();
		EventListener eventListener = new EventListener(this, this._bungeeController);
		Controller controller = new Controller(this._bungeeController);
		this._api = new EithonLibraryApi(controller);
		super.activate(null, eventListener);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		AlarmTrigger.get().disable();
	}
	
	public EithonLibraryApi getApi() {return this._api; }
}

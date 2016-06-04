package net.eithon.plugin.eithonlibrary;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.facades.PermissionsFacade;
import net.eithon.library.plugin.GeneralMessage;
import net.eithon.library.plugin.Logger;
import net.eithon.library.time.AlarmTrigger;

import org.bukkit.event.Listener;

public final class EithonLibraryPlugin extends EithonPlugin implements Listener {
	
	@Override
	public void onEnable() {
		super.onEnable();
		Logger logger = getEithonLogger();
		Logger.setDefaultDebug(logger);
		Config.load(this);
		EventListener eventListener = new EventListener(this);
		super.activate(eventListener);
		GeneralMessage.initialize(this);
		AlarmTrigger.get().enable(this);
		PermissionsFacade.initialize(this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		AlarmTrigger.get().disable();
	}
}

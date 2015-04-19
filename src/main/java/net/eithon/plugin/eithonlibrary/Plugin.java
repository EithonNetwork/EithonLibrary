package net.eithon.plugin.eithonlibrary;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.GeneralMessage;
import net.eithon.library.plugin.Logger;

public final class Plugin extends EithonPlugin {
	@Override
	public void onEnable() {
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
}

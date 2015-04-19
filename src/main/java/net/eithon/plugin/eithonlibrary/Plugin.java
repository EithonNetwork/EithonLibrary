package net.eithon.plugin.eithonlibrary;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.GeneralMessage;
import net.eithon.library.plugin.Logger;

public final class Plugin extends EithonPlugin {
	@Override
	public void onEnable() {
		Logger logger = getEithonLogger();
		Logger.setDefaultDebug(logger);
		GeneralMessage.initialize(this);
	}

	@Override
	public void onDisable() {
	}
}

package net.eithon.plugin.eithonlibrary;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.GeneralMessage;
import net.eithon.library.plugin.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		EithonPlugin eithonPlugin = EithonPlugin.get(this);
		eithonPlugin.enable();
		Logger debug = eithonPlugin.getLogger();
		Logger.setDefaultDebug(debug);
		GeneralMessage.initialize(eithonPlugin);
	}

	@Override
	public void onDisable() {
	}
}

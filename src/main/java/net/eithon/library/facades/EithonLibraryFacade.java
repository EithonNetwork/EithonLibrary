package net.eithon.library.facades;

import org.bukkit.plugin.Plugin;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.PluginMisc;
import net.eithon.plugin.eithonlibrary.EithonLibraryApi;
import net.eithon.plugin.eithonlibrary.EithonLibraryPlugin;

public class EithonLibraryFacade {

	private EithonLibraryPlugin _eithonLibraryPlugin;
	
	public EithonLibraryFacade(EithonPlugin eithonPlugin) {
		connect(eithonPlugin);
	}
	
	public EithonLibraryApi getApi()  {
		return this._eithonLibraryPlugin.getApi();
	}

	private void connect(EithonPlugin eithonPlugin) {
		Plugin plugin = PluginMisc.getPlugin("EithonLibrary");
		if (plugin != null && plugin.isEnabled()) {
			eithonPlugin.getEithonLogger().info("Succesfully hooked into the EithonLibrary plugin!");
		} else {
			eithonPlugin.getEithonLogger().warning("Could not hook into the EithonLibrary plugin");
			return;
		}
		this._eithonLibraryPlugin = (EithonLibraryPlugin) plugin;
	}
}

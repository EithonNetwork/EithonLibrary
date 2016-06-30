package net.eithon.library.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PluginMisc {	
	public static boolean isPluginEnabled(String pluginName) {
		Server server = Bukkit.getServer();
		if (server == null) return false;

		PluginManager pluginManager = server.getPluginManager();
		if (pluginManager == null) return false;
		
		Plugin plugin = pluginManager.getPlugin(pluginName);
		return (plugin != null && plugin.isEnabled());
	}
	
	public static Plugin getPlugin(String pluginName) {
		Server server = Bukkit.getServer();
		if (server == null) return null;

		PluginManager pluginManager = server.getPluginManager();
		if (pluginManager == null) return null;
		
		return pluginManager.getPlugin(pluginName);
	}
}

package net.eithon.library.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PluginMisc {

	@Deprecated
	// Moved to CommandParser
	public static boolean isPlayerOrWarn(CommandSender sender) {
		if (sender instanceof Player) return true;
		
		GeneralMessage.expectedToBePlayer.sendMessage(sender, sender.getName());
		return false;	
	}
	
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

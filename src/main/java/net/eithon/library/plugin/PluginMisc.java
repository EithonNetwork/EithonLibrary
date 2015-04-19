package net.eithon.library.plugin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PluginMisc {

	@Deprecated
	// Moved to CommandParser
	public static boolean isPlayerOrWarn(CommandSender sender) {
		if (sender instanceof Player) return true;
		
		GeneralMessage.expectedToBePlayer.sendMessage(sender, sender.getName());
		return false;	
	}
}

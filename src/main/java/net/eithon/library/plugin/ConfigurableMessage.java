package net.eithon.library.plugin;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.command.CommandSender;

public class ConfigurableMessage extends ConfigurableFormat{

	ConfigurableMessage(EithonPlugin eithonPlugin, String path, int parameters, String defaultValue)
	{
		super(eithonPlugin, path, parameters, defaultValue);
	}
	
	public boolean sendMessage(CommandSender sender, Object... args) {
		String message = getMessageWithColorCoding(args);
		if (message == null) return false;
		sender.sendMessage(message);
		return true;
	}
	
	public void broadcastMessage(Object... args) {
		String message = getMessageWithColorCoding(args);
		this._eithonPlugin.getServer().broadcastMessage(message);
	}
}

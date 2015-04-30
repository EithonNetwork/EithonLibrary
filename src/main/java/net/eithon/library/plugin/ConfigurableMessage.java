package net.eithon.library.plugin;

import java.util.HashMap;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.command.CommandSender;

public class ConfigurableMessage extends ConfigurableFormat{

	ConfigurableMessage(EithonPlugin eithonPlugin, String path, int parameters, String defaultValue, String... parameterNames)
	{
		super(eithonPlugin, path, parameters, defaultValue, parameterNames);
	}

	public boolean sendMessage(CommandSender sender, Object... args) {
		String message = getMessageWithColorCoding(args);
		if (message == null) return false;
		sender.sendMessage(message);
		return true;
	}

	public boolean sendMessage(CommandSender sender, HashMap<String,String> namedParameters, Object... positionalParameters) {
		String message = getMessageWithColorCoding(namedParameters, positionalParameters);
		if (message == null) return false;
		sender.sendMessage(message);
		return true;
	}
	
	public void broadcastMessage(Object... args) {
		String message = getMessageWithColorCoding(args);
		this._eithonPlugin.getServer().broadcastMessage(message);
	}
	
	public void broadcastMessage(HashMap<String,String> namedParameters, Object... positionalParameters) {
		String message = getMessageWithColorCoding(namedParameters, positionalParameters);
		this._eithonPlugin.getServer().broadcastMessage(message);
	}
}

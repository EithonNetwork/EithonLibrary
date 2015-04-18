package net.eithon.library.plugin;

import org.bukkit.command.CommandSender;

public class ConfigurableMessage extends ConfigurableFormat{

	ConfigurableMessage(Configuration config, String path, int parameters, String defaultValue)
	{
		super(config, path, parameters, defaultValue);
	}
	
	public boolean sendMessage(CommandSender sender, Object... args) {
		String message = getMessage(args);
		if (message == null) return false;
		sender.sendMessage(message);
		return true;
	}
}

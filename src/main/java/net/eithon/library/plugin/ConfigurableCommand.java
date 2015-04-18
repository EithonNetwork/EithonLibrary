package net.eithon.library.plugin;

import org.bukkit.Bukkit;

public class ConfigurableCommand extends ConfigurableFormat{

	ConfigurableCommand(Configuration config, String path, int parameters, String defaultValue)
	{
		super(config, path, parameters, defaultValue);
	}
	
	public void execute(Object... args) {
		String command = getMessage(args);
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
	}
}

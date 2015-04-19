package net.eithon.library.plugin;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.Bukkit;

public class ConfigurableCommand extends ConfigurableFormat{

	ConfigurableCommand(EithonPlugin eithonPlugin, String path, int parameters, String defaultValue)
	{
		super(eithonPlugin, path, parameters, defaultValue);
	}
	
	public void execute(Object... args) {
		String command = getMessage(args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.MAJOR, "/%s", command);
		this._eithonPlugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
	}
}

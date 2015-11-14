package net.eithon.library.plugin;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.EithonLogger.DebugPrintLevel;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ConfigurableCommand extends ConfigurableFormat{

	ConfigurableCommand(EithonPlugin eithonPlugin, String path, int parameters, String defaultValue)
	{
		super(eithonPlugin, path, parameters, defaultValue);
	}
	
	private void executeCommandAs(String command, CommandSender sender) {
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.MAJOR, "/%s", command);
		this._eithonPlugin.getServer().dispatchCommand(sender, command);
	}
	
	public void executeAs(CommandSender sender, Object... args) {
		String command = getMessage(args);
		if (command.isEmpty()) return;
		executeCommandAs(command, sender);
	}
	
	public void execute(Object... args) {
		String command = getMessage(args);
		if (command.isEmpty()) return;
		executeCommandAs(command, Bukkit.getServer().getConsoleSender());
	}
}

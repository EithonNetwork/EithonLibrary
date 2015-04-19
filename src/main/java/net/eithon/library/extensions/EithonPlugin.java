package net.eithon.library.extensions;

import java.io.File;
import java.util.HashMap;

import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.CommandParser;
import net.eithon.library.plugin.ConfigurableCommand;
import net.eithon.library.plugin.ConfigurableMessage;
import net.eithon.library.plugin.Configuration;
import net.eithon.library.plugin.ICommandHandler;
import net.eithon.library.plugin.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EithonPlugin extends JavaPlugin {
	private static HashMap<String, EithonPlugin> instances = new HashMap<String, EithonPlugin>();
	private Logger _logger;
	private Configuration _config;
	private ICommandHandler _commandHandler;
	private Listener _eventListener;

	public EithonPlugin() {}
	
	@Override
	public void onEnable() {
		enable(null, null);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		CommandParser commandParser = new CommandParser(this._commandHandler, sender, cmd, label, args);
		return commandParser.execute();
	}

	public void enable(ICommandHandler commandHandler, Listener eventListener) {
		this._logger = new Logger(this);
		this._config = new Configuration(this);
		this._config.enable();
		this._logger.enable();
		instances.put(getName(), this);
		this._commandHandler = commandHandler;
		this._eventListener = eventListener;
		getServer().getPluginManager().registerEvents(this._eventListener, this);
	}

	@Override
	public void onDisable() {
		this._commandHandler = null;
		this._eventListener = null;
	}
	
	@Deprecated
	public static EithonPlugin get(JavaPlugin plugin) {
		EithonPlugin eithonPlugin = getByName(plugin.getName());
		return eithonPlugin;
	}
	
	@Deprecated
	public static EithonPlugin getByName(String name) {
		return instances.get(name);
	}

	public Configuration getConfiguration() { return this._config; }

	public Logger getEithonLogger() { return this._logger; }

	public ConfigurableMessage getConfigurableMessage(String path, int parameters, String defaultValue) {
		return this._config.getConfigurableMessage(path, parameters, defaultValue);
	}
	
	public ConfigurableCommand getConfigurableCommand(String path, int parameters, String defaultValue) {
		return this._config.getConfigurableCommand(path, parameters, defaultValue);
	}
	
	public File getDataFile(String fileName) {
		return FileMisc.getPluginDataFile(this, fileName);
	}
}

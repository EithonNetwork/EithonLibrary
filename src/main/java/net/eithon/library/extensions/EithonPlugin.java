package net.eithon.library.extensions;

import java.io.File;
import java.util.HashMap;

import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.ConfigurableCommand;
import net.eithon.library.plugin.ConfigurableMessage;
import net.eithon.library.plugin.Configuration;
import net.eithon.library.plugin.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class EithonPlugin {
	private JavaPlugin _plugin;
	private static HashMap<String, EithonPlugin> instances = new HashMap<String, EithonPlugin>();
	private Logger _logger;
	private Configuration _config;

	private EithonPlugin(JavaPlugin plugin) { 
		this._plugin = plugin;
		this._logger = new Logger(this);
		this._config = new Configuration(this);
		instances.put(plugin.getName(), this);
	}
	
	public static EithonPlugin get(JavaPlugin plugin) {
		EithonPlugin eithonPlugin = getByName(plugin.getName());
		if (eithonPlugin != null) return eithonPlugin;
		return new EithonPlugin(plugin);
	}
	
	public static EithonPlugin getByName(String name) {
		return instances.get(name);
	}

	public void enable() {
		this._config.enable();
		this._logger.enable();
	}


	public void disable() {
	}

	public JavaPlugin getJavaPlugin() { return this._plugin; }

	public Configuration getConfiguration() { return this._config; }

	public Logger getLogger() { return this._logger; }

	public ConfigurableMessage getConfigurableMessage(String path, int parameters, String defaultValue) {
		return this._config.getConfigurableMessage(path, parameters, defaultValue);
	}
	
	public ConfigurableCommand getConfigurableCommand(String path, int parameters, String defaultValue) {
		return this._config.getConfigurableCommand(path, parameters, defaultValue);
	}
	
	public File getDataFile(String fileName) {
		return FileMisc.getPluginDataFile(this._plugin, fileName);
	}
}

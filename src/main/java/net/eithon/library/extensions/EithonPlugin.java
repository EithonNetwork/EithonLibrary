package net.eithon.library.extensions;

import java.util.HashMap;

import net.eithon.library.misc.Debug;
import net.eithon.library.plugin.ConfigurableCommand;
import net.eithon.library.plugin.ConfigurableMessage;
import net.eithon.library.plugin.Configuration;

import org.bukkit.plugin.java.JavaPlugin;

public class EithonPlugin {
	private JavaPlugin _plugin;
	private static HashMap<String, EithonPlugin> instances = new HashMap<String, EithonPlugin>();
	private Debug _debug;
	private Configuration _config;

	private EithonPlugin(JavaPlugin plugin) { 
		this._plugin = plugin;
		this._debug = new Debug(this);
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
		this._debug.enable();
	}


	public void disable() {
	}

	public JavaPlugin getJavaPlugin() { return this._plugin; }

	public Configuration getConfiguration() { return this._config; }

	public Debug getDebug() { return this._debug; }

	public ConfigurableMessage getConfigurableMessage(String path, int parameters, String defaultValue) {
		return this._config.getConfigurableMessage(path, parameters, defaultValue);
	}
	
	public ConfigurableCommand getConfigurableCommand(String path, int parameters, String defaultValue) {
		return this._config.getConfigurableCommand(path, parameters, defaultValue);
	}
}

package net.eithon.library.extensions;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.eithon.library.command.ICommandSyntax;
import net.eithon.library.core.CoreMisc;
import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.Configuration;
import net.eithon.library.plugin.ICommandHandler;
import net.eithon.library.plugin.Logger;
import net.eithon.library.plugin.PermissionBasedMultiplier;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EithonPlugin extends JavaPlugin implements Listener, TabCompleter {
	private static HashMap<String, EithonPlugin> instances = new HashMap<String, EithonPlugin>();
	private Logger _logger;
	private Configuration _config;
	private ICommandHandler _commandHandlerOld;
	private ICommandSyntax _commandSyntax;

	public EithonPlugin() {}

	@Override
	public void onEnable() {
		Logger.initialize();
		PermissionBasedMultiplier.initialize();
		this._logger = new Logger(this);
		this._config = new Configuration(this);
		this._config.enable();
		this._logger.enable();
		instances.put(getName(), this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ((this._commandHandlerOld == null) && (this._commandSyntax == null)) {
			this._logger.error("EithonPlugin has not been activated, can't execute command \"%s\".", label);
			return false;
		}
		if (this._commandHandlerOld != null) {
			return new net.eithon.library.plugin.CommandParser(this._commandHandlerOld, sender, cmd, label, args)
			.execute();
		}
		return new net.eithon.library.command.EithonCommand(this._commandSyntax, sender, cmd, label, args)
		.execute();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		Queue<String> argumentQueue = new LinkedList<String>();
		argumentQueue.addAll(Arrays.asList(args));
		argumentQueue.poll();
		return new net.eithon.library.command.EithonCommand(this._commandSyntax, sender, cmd, alias, args)
		.tabComplete();
	}

	public void activate(Listener... eventListeners) {
		this._commandSyntax = null;
		this._commandHandlerOld = null;
		PluginManager pluginManager = getServer().getPluginManager();
		if (eventListeners.length == 0) {
			pluginManager.registerEvents(this, this);
			return;
		}
		for (Listener listener : eventListeners) {
			try {
				pluginManager.registerEvents(listener , this);				
			} catch (Exception e) {
				logError("%s", e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Deprecated
	public void activate(ICommandHandler commandHandler, Listener... eventListeners) {
		activate(eventListeners);
		this._commandHandlerOld = commandHandler;
	}

	public void activate(ICommandSyntax commandSyntax, Listener... eventListeners) {
		activate(eventListeners);
		this._commandSyntax = commandSyntax;
		String commandName = commandSyntax.getName();
		PluginCommand command = getCommand(commandName);
		if (command == null) {
			this.logError("CommandSyntax name %s is not a command of this plugin", commandName);
			return;
		}
		command.setTabCompleter(this);
	}

	@Override
	public void onDisable() {
		this._commandHandlerOld = null;
		this._commandSyntax = null;
		instances.remove(getName());
	}

	public static EithonPlugin getByName(String name) {
		return instances.get(name);
	}

	public Configuration getConfiguration() { return this._config; }

	@Deprecated
	public Logger getEithonLogger() { return this._logger; }

	public void setDebugLevel(int level) { this._logger.setDebugLevel(level); }
	public void logError(String format, Object... args) { this._logger.error(format, args); }
	public void logWarn(String format, Object... args) { this._logger.warning(format, args); }
	public void logInfo(String format, Object... args) { this._logger.info(format, args); }
	public void dbgMajor(String format, Object... args) { this._logger.debug(DebugPrintLevel.MAJOR, format, args); }
	public void dbgMinor(String format, Object... args) { this._logger.debug(DebugPrintLevel.MINOR, format, args); }
	public void dbgVerbose(String className, String method, String format, Object... args) {
		String message = CoreMisc.safeFormat(format, args);
		this._logger.debug(DebugPrintLevel.VERBOSE, "%s.%s: %s", className, method, message);
	}

	public File getDataFile(String fileName) {
		return FileMisc.getPluginDataFile(this, fileName);
	}
}

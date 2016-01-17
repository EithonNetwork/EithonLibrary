package net.eithon.library.extensions;

import java.io.File;
import java.util.HashMap;

import net.eithon.library.command.CommandSyntax;
import net.eithon.library.facades.EithonLibraryFacade;
import net.eithon.library.facades.ZPermissionsFacade;
import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.Configuration;
import net.eithon.library.plugin.GeneralMessage;
import net.eithon.library.plugin.ICommandHandler;
import net.eithon.library.plugin.Logger;
import net.eithon.library.plugin.PermissionBasedMultiplier;
import net.eithon.library.time.AlarmTrigger;
import net.eithon.plugin.eithonlibrary.EithonLibraryApi;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EithonPlugin extends JavaPlugin implements Listener {
	private static HashMap<String, EithonPlugin> instances = new HashMap<String, EithonPlugin>();
	private Logger _logger;
	private Configuration _config;
	private ICommandHandler _commandHandlerOld;
	private CommandSyntax _commandSyntax;
	private Listener _eventListener;
	private EithonLibraryApi _eithonLibraryApi;

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
		GeneralMessage.initialize(this);
		AlarmTrigger.get().enable(this);
		ZPermissionsFacade.initialize(this);
		this._eithonLibraryApi = new EithonLibraryFacade(this).getApi();
	}

	public EithonLibraryApi getApi() { return this._eithonLibraryApi;	}

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

	public void activate(Listener eventListener) {
		this._commandSyntax = null;
		this._commandHandlerOld = null;
		this._eventListener = eventListener;
		if (this._eventListener == null) this._eventListener = this;
		getServer().getPluginManager().registerEvents(this._eventListener, this);
	}

	@Deprecated
	public void activate(ICommandHandler commandHandler, Listener eventListener) {
		this._commandHandlerOld = commandHandler;
		this._eventListener = eventListener;
		if (this._eventListener == null) this._eventListener = this;
		getServer().getPluginManager().registerEvents(this._eventListener, this);
	}

	public void activate(CommandSyntax commandSyntax, Listener eventListener) {
		this._commandSyntax = commandSyntax;
		this._eventListener = eventListener;
		if (this._eventListener == null) this._eventListener = this;
		getServer().getPluginManager().registerEvents(this._eventListener, this);
	}

	@Override
	public void onDisable() {
		this._commandHandlerOld = null;
		this._commandSyntax = null;
		this._eventListener = null;
		instances.remove(getName());
	}

	public static EithonPlugin getByName(String name) {
		return instances.get(name);
	}

	public Configuration getConfiguration() { return this._config; }

	public Logger getEithonLogger() { return this._logger; }

	public File getDataFile(String fileName) {
		return FileMisc.getPluginDataFile(this, fileName);
	}
}

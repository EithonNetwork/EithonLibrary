package net.eithon.library.plugin;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

abstract class ConfigurableFormat {
	private String _path;
	private int _parameters;
	private String _formatValue;
	protected EithonPlugin _eithonPlugin;
	
	ConfigurableFormat(EithonPlugin eithonPlugin, String path, int parameters, String defaultValue) {
		this._path = path;
		this._parameters = parameters;
		this._eithonPlugin = eithonPlugin;
		Configuration config = eithonPlugin.getConfiguration();
		String value = config.getString(path, defaultValue);
		this._formatValue = value;
	}

	public String getFormat() {
		return this._formatValue;
	}

	public String getMessage(Object... args) {
		return CoreMisc.safeFormat(this._formatValue, args);
	}

	public String getMessageWithColorCoding(Object... args) {
		String beforeColors = CoreMisc.safeFormat(this._formatValue, args);
		return ChatColor.translateAlternateColorCodes('&', beforeColors);
	}

	public void reportFailure(CommandSender sender, Exception e) {
		String message = String.format(
				"The %s (\"%s\") from config.yml is not correctly formatted. Verify that the %d parameter(s) are correctly used.",
				this._path, this._formatValue, this._parameters, e.getMessage());
		if (e != null) {
			message = String.format("%s\rThis was the exception message:\r%s", message, e.getMessage());
		}
		Bukkit.getLogger().warning(message);
		if (sender != null) {
			sender.sendMessage(message);
		}
	}
}

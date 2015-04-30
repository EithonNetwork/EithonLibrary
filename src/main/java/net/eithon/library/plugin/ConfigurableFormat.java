package net.eithon.library.plugin;

import java.util.HashMap;

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
	String[] _parameterNames;
	
	ConfigurableFormat(EithonPlugin eithonPlugin, String path, int parameters, String defaultValue, String... parameterNames) {
		this._eithonPlugin = eithonPlugin;
		this._path = path;
		Configuration config = eithonPlugin.getConfiguration();
		String value = config.getString(path, defaultValue);
		this._formatValue = value;
		this._parameters = parameters;
		this._parameterNames = parameterNames;
	}

	public String getFormat() {
		return this._formatValue;
	}

	public boolean hasContent() {
		return (this._formatValue != null) && (this._formatValue.length() > 0);
	}

	public String getMessage(Object... args) {
		if (!hasContent()) return "";
		return CoreMisc.safeFormat(this._formatValue, args);
	}

	public String getMessage(HashMap<String,String> namedArguments, Object... positionalArguments) {
		if (!hasContent()) return "";
		if (this._parameterNames == null) return getMessage(positionalArguments);
		String formatValue = replaceParameters(this._formatValue, namedArguments);
		return CoreMisc.safeFormat(formatValue, positionalArguments);
	}

	private String replaceParameters(String format,
			HashMap<String, String> arguments) {
		for (String parameterName : arguments.keySet()) {
			String formalName = "%" + parameterName + "%";
			format = format.replace(formalName, arguments.get(parameterName));
		}
		return format;
	}

	public String getMessageWithColorCoding(Object... args) {
		String beforeColors = getMessage(args);
		return ChatColor.translateAlternateColorCodes('&', beforeColors);
	}

	public String getMessageWithColorCoding(HashMap<String,String> namedArguments, Object... positionalArguments) {
		String beforeColors = getMessage(namedArguments, positionalArguments);
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

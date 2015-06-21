package net.eithon.library.plugin;

import net.eithon.library.chat.LineWrapper;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.command.CommandSender;

public class ConfigurableMessage extends ConfigurableFormat{
	
	private boolean _useWrapping;

	ConfigurableMessage(EithonPlugin eithonPlugin, String path, int parameters, String defaultValue, String... parameterNames)
	{
		super(eithonPlugin, path, parameters, defaultValue, parameterNames);
		Configuration config = eithonPlugin.getConfiguration();
		this._useWrapping = config.getInt("eithon.UseWrappingForMessages", 0) > 0;
	}

	public boolean sendMessage(CommandSender sender, Object... args) {
		if (sender == null) return false;
		String message = getMessageWithColorCoding(args);
		if (message == null) return false;
		if (this._useWrapping) {
			this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "Wrapping \"%s\".", message);
			String[] messageArray = LineWrapper.wrapLine(message, 320);
			sender.sendMessage(messageArray);
		} else {
			this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "sendMessage \"%s\".", message);
			sender.sendMessage(message);
		}
		return true;
	}
	
	public void broadcastMessage(Object... args) {
		String message = getMessageWithColorCoding(args);
		this._eithonPlugin.getServer().broadcastMessage(message);
	}
}

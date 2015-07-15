package net.eithon.library.plugin;

import net.eithon.library.chat.LineWrapper;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.library.title.Title;
import net.eithon.plugin.eithonlibrary.Config;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigurableMessage extends ConfigurableFormat{

	private boolean _useWrapping;
	private boolean _useTitle;

	ConfigurableMessage(EithonPlugin eithonPlugin, String path, int parameters, String defaultValue, String... parameterNames)
	{
		super(eithonPlugin, path, parameters, defaultValue, parameterNames);
		Configuration config = eithonPlugin.getConfiguration();
		this._useTitle = shouldTitleBeUsed(this._formatValue);
		if (this._useTitle) this._formatValue = this._formatValue.substring(1);
		this._useWrapping = config.getInt("eithon.UseWrappingForMessages", 0) > 0;
	}

	public boolean sendMessage(CommandSender sender, Object... args) {
		if (sender == null) return false;
		String message = getMessageWithColorCoding(args);
		if (message == null) return false;
		if (this._useTitle && (sender instanceof Player)) {
			sendTitle((Player) sender, message);
		} else {
			if (this._useWrapping) {
				this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "Wrapping \"%s\".", message);
				String[] messageArray = LineWrapper.wrapLine(message, 320);
				sender.sendMessage(messageArray);
			} else {
				this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "sendMessage \"%s\".", message);
				sender.sendMessage(message);
			}
		}
		return true;
	}

	private static boolean shouldTitleBeUsed(String format) {
		if (format == null) return false;
		return format.startsWith("!");
	}

	private void sendTitle(Player player, String message) {
		String[] lines = message.split("\\n");
		String title = lines[0];
		String subTitle = lines.length > 1 ? lines[1] : null;
		Title.get().sendFloatingText(player, title, subTitle, Config.V.titleFadeInTicks, Config.V.titleStayTicks, Config.V.titleFadeOutTicks);
	}

	public void broadcastMessage(Object... args) {
		String message = getMessageWithColorCoding(args);
		this._eithonPlugin.getServer().broadcastMessage(message);
	}
}

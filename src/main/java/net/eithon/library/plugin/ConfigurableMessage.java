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
		if (this._useTitle) {
			if (this._formatValue.startsWith("[title/]")) {
				this._formatValue = this._formatValue.substring(8);
			}
			if (this._formatValue.contains("[subtitle/]")) {
				this._formatValue = this._formatValue.replace("[subtitle/]", "\n");
				this._formatValue = this._formatValue.replace("[actionbar/]", "\n");
			} else {
				this._formatValue = this._formatValue.replace("[actionbar/]", "\n\n");
			}
			eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "FormatValue: \"%s\"", this._formatValue);
		}
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
				String[] messageArray = LineWrapper.wrapLine(message, 320);
				sender.sendMessage(messageArray);
			} else {
				sender.sendMessage(message);
			}
		}
		return true;
	}

	private static boolean shouldTitleBeUsed(String format) {
		if (format == null) return false;
		return format.startsWith("[title/]") || format.startsWith("[subtitle/]") || format.startsWith("[actionbar/]");
	}

	private void sendTitle(Player player, String message) {
		String[] lines = message.split("\\n");
		String title = lines[0];
		String subTitle = lines.length > 1 ? lines[1] : "";
		String actionBar = lines.length > 2 ? lines[2] : "";
		if (!title.equals("") || !subTitle.equals("")) Title.get().sendFloatingText(player, title, subTitle, Config.V.titleFadeInTicks, Config.V.titleStayTicks, Config.V.titleFadeOutTicks);
		if (!actionBar.equals("")) Title.get().sendActionbarMessage(player, actionBar);
	}

	private void sendTitle(String message) {
		sendTitle(null, message);
	}

	public boolean broadcastMessage(Object... args) {
		String message = getMessageWithColorCoding(args);
		if (message == null) return false;
		broadcastToThisServer(message);
		return true;
	}

	public void broadcastToThisServer(String message) {
		if (message == null) return;
		if (this._useTitle) {
			sendTitle(message);
		} else {
			this._eithonPlugin.getServer().broadcastMessage(message);
		}
	}

	public boolean broadcastMessageToAllServers(Object... args) {
		String message = getMessageWithColorCoding(args);
		if (message == null) return false;
		broadcastToThisServer(message);
		return this._eithonPlugin.getApi().bungeeBroadcastMessage(message, this._useTitle);
	}
}

package net.eithon.library.plugin;

import net.eithon.library.chat.LineWrapper;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.title.Title;
import net.eithon.plugin.eithonlibrary.Config;

import org.bukkit.Bukkit;
import org.bukkit.Server;
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
			verbose("ConfigurableMessage", "FormatValue: \"%s\"", this._formatValue);
		}
		this._useWrapping = config.getInt("eithon.UseWrappingForMessages", 0) > 0;
	}

	public boolean getUseTitle() { return this._useTitle;}

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

	private static void sendTitle(Player player, String message) {
		String[] lines = message.split("\\n");
		String title = lines[0];
		String subTitle = lines.length > 1 ? lines[1] : "";
		String actionBar = lines.length > 2 ? lines[2] : "";
		if (!title.equals("") || !subTitle.equals("")) Title.get().sendFloatingText(player, title, subTitle, Config.V.titleFadeInTicks, Config.V.titleStayTicks, Config.V.titleFadeOutTicks);
		if (!actionBar.equals("")) Title.get().sendActionbarMessage(player, actionBar);
	}

	private static void sendTitle(String message) {
		sendTitle(null, message);
	}
	
	public boolean broadcastToThisServer(Object... args) {
		String message = getMessageWithColorCoding(args);
		if (message == null) return false;
		ConfigurableMessage.broadcastToThisServer(message, this._useTitle);
		return true;
	}

	public static void broadcastToThisServer(String message, boolean useTitle) {
		if (useTitle) {
			sendTitle(message);
		} else {
			Server server = Bukkit.getServer();
			if (server == null) return;
			server.broadcastMessage(message);
		}
	}
	public boolean broadcastToAllServers(Object... args) {
		String message = getMessageWithColorCoding(args);
		if (message == null) return false;
		EithonPublicMessageEvent e = new EithonPublicMessageEvent(message, this._useTitle);
		this._eithonPlugin.getServer().getPluginManager().callEvent(e);
		return true;
	}

	private void verbose(String method, String format, Object... args) {
		super.verboseLog("ConfigurableMessage", method, format, args);	
	}
}

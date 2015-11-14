package net.eithon.library.bungee;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.json.IJsonObject;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.entity.Player;

class BungeeSender {
	private EithonPlugin _eithonPlugin;
	private Channel _messageChannel;

	public BungeeSender(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
		this._messageChannel = new Channel(eithonPlugin);
	}

	boolean forwardToAll(String command, IJsonObject<?> info, boolean rejectOld) {
		return forward("ALL", command, info, rejectOld);
	}

	boolean forward(String destinationServer, String command, IJsonObject<?> info, boolean rejectOld) {
		verbose("forward", "Enter; destinationServer=%s, command = %s", destinationServer, command);
		String sourceServerName =  this._eithonPlugin.getApi().getBungeeServerName();
		ForwardHeader header = new ForwardHeader(command, sourceServerName, rejectOld);
		verbose("send", "header = %s", header.toJSONString());
		MessageOut data = new MessageOut()
		.add(header.toJSONString())
		.add(info.toJsonString());
		boolean success = this._messageChannel.send("Forward", data, destinationServer, "EithonLibraryForward");
		verbose("forward", "Leave, success = %s", success ? "TRUE" : "FALSE");
		return success;
	}

	boolean getServer() {
		return this._messageChannel.send("GetServer");
	}

	boolean connect(Player player, String serverName) {
		return this._messageChannel.send(player, "Connect", serverName);
	}

	private void verbose(String method, String format, Object... args) {
		String message = CoreMisc.safeFormat(format, args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "BungeeSender.%s: %s", method, message);
	}
}

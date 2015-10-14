package net.eithon.library.bungee;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.entity.Player;

class BungeeSender {
	private EithonPlugin _eithonPlugin;
	private Channel _messageChannel;

	public BungeeSender(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
		this._messageChannel = new Channel(eithonPlugin);
	}

	boolean forwardToAll(String command, String jsonString, boolean rejectOld) {
		return forward("ALL", command, jsonString, rejectOld);
	}

	boolean forward(String server, String command, String jsonString, boolean rejectOld) {
		String sourceServerName = this._eithonPlugin.getApi().getBungeeServerName();
		ForwardHeader forwardHeader = new ForwardHeader(command, sourceServerName, rejectOld);
		MessageOut msgout = new MessageOut()
		.add(forwardHeader.toJSONString(), jsonString);
		return this._messageChannel.send("Forward", msgout.toByteArray());
	}

	boolean getServer() {
		return this._messageChannel.send("GetServer");
	}

	boolean connect(Player player, String serverName) {
		return this._messageChannel.send(player, "Connect", serverName);
	}
}

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

	boolean forward(String destinationServer, String command, String body, boolean rejectOld) {
		String sourceServerName = this._eithonPlugin.getApi().getBungeeServerName();
		ForwardHeader header = new ForwardHeader(command, sourceServerName, rejectOld);
		MessageOut data = new MessageOut()
		.add(header.toJSONString())
		.add(body);
		MessageOut msgout = new MessageOut()
		.add(destinationServer)
		.add("EithonLibraryForward")
		.add(data.toByteArray());
		return this._messageChannel.send("Forward", msgout);
	}

	boolean getServer() {
		return this._messageChannel.send("GetServer");
	}

	boolean connect(Player player, String serverName) {
		return this._messageChannel.send(player, "Connect", serverName);
	}
}

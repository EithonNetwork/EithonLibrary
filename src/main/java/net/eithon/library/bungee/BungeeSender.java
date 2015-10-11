package net.eithon.library.bungee;

import java.io.ByteArrayOutputStream;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

class BungeeSender {
	private EithonPlugin _eithonPlugin;

	public BungeeSender(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
	}
	
	boolean forwardToAll(String subChannel, ByteArrayOutputStream msgbytes) {
		return forward("ALL", subChannel, msgbytes);
	}
	
	boolean forward(String server, String subChannel, ByteArrayOutputStream msgbytes) {
		return send("Forward", msgbytes, server, subChannel);
	}

	boolean getServer() {
		return send("GetServer");
	}

	boolean connect(Player player, String serverName) {
		return send(player, "Connect", serverName);
	}

	private void verbose(String method, String format, Object... args) {
		String message = CoreMisc.safeFormat(format, args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "BungeeSender.%s: %s", method, message);
	}

	private boolean send(Player player, String subChannel, ByteArrayOutputStream message, String... arguments) {
		verbose("send", String.format("Enter: Player=%s, subChannel=%s, message=%s", 
				player == null? "NULL" : player.getName(), subChannel, message == null ? "NULL" : message.toString()));
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(subChannel);
		for (String argument : arguments) {
			verbose("send", String.format("Argument=%s", argument));
			out.writeUTF(argument);
		}

		if (message != null) {
			out.writeShort(message.toByteArray().length);
			out.write(message.toByteArray());
		}

		player.sendPluginMessage(this._eithonPlugin, "BungeeCord", out.toByteArray());
		verbose("send", "Leave TRUE");
		return true;
	}

	private boolean send(String subChannel, ByteArrayOutputStream message, String... arguments) {
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		return send(player, subChannel, message, arguments);
	}

	private boolean send(Player player, String subChannel, String... arguments) {
		return send(player, subChannel, null, arguments);
	}

	private boolean send(String subChannel, String... arguments) {
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		return send(player, subChannel, null, arguments);
	}
}

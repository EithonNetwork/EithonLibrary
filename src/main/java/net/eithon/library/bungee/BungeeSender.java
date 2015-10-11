package net.eithon.library.bungee;

import java.io.ByteArrayOutputStream;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.library.time.TimeMisc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

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
		if (player == null) {
			verbose("send", "Player was null");
			verbose("send", "Leave FALSE");
			return false;
		}
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

	boolean send(String subChannel, ByteArrayOutputStream message, String... arguments) {
		verbose("send", String.format("Enter: subChannel=%s, message=%s", 
				subChannel, message == null ? "NULL" : message.toString()));
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if (player == null) {
			verbose("send", "No player, will try again in one second.");
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(this._eithonPlugin, new Runnable() {
				public void run() {
					send(subChannel, message, arguments);
				}
			}, TimeMisc.secondsToTicks(1));
			verbose("send", "Leave, TRUE");
			return true;
		}
		boolean success = send(player, subChannel, message, arguments);
		verbose("send", String.format("Leave, success = %s", success ? "TRUE" : "FALSE"));
		return success;
	}

	private boolean send(Player player, String subChannel, String... arguments) {
		return send(player, subChannel, null, arguments);
	}

	private boolean send(String subChannel, String... arguments) {
		return send(subChannel, null, arguments);
	}
}

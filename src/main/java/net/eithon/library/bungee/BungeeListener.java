package net.eithon.library.bungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

class BungeeListener implements PluginMessageListener {

	private EithonPlugin _eithonPlugin;
	private BungeeController _controller;

	public BungeeListener(EithonPlugin eithonPlugin, BungeeController controller) {
		this._eithonPlugin = eithonPlugin;
		this._controller = controller;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		verbose("onPluginMessageReceived", String.format("Enter: channel=%s, player=%s, message=%s",
				channel, player == null ? "NULL" : player.getName(), message.toString()));
		if (!channel.equals("BungeeCord")) {
			return;
		}

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		verbose("onPluginMessageReceived", String.format("subchannel=%s", subchannel));
		if (subchannel.equals("GetServer")) {
			getServer(in, subchannel);
		} else if (subchannel.equals("EithonBungeeJoinEvent")) {
			short len = in.readShort();
			verbose("onPluginMessageReceived", String.format("len=%d", len));
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			eithonBungeeJoinEvent(msgbytes);
		} else if (subchannel.equals("EithonBungeeQuitEvent")) {
			short len = in.readShort();
			verbose("onPluginMessageReceived", String.format("len=%d", len));
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			eithonBungeeQuitEvent(msgbytes);
		}
		verbose("onPluginMessageReceived", "Leave");
	}

	private void getServer(ByteArrayDataInput in, String subchannel) {
		verbose("getServer", "Enter");
		String serverName = in.readUTF();
		verbose("getServer", String.format("serverName=%s", serverName));
		this._controller.setServerName(serverName);
		verbose("getServer", "Leave");
	}

	private void eithonBungeeJoinEvent(byte[] msgbytes) {
		verbose("eithonBungeeJoinEvent", String.format("Enter msgbytes=%s", msgbytes.toString()));
		DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
		try {
			String serverName = msgin.readUTF();
			verbose("eithonBungeeJoinEvent", String.format("serverName=%s", serverName));
			if (serverName.isEmpty()) serverName = null;
			String uuid = msgin.readUTF();
			verbose("eithonBungeeJoinEvent", String.format("player uuid=%s", uuid));
			UUID playerId = UUID.fromString(uuid);
			EithonPlayer player = new EithonPlayer(playerId);
			if (player.getOfflinePlayer() == null) {
				verbose("eithonBungeeJoinEvent", "No user found, Leave");
				return;				
			}
			verbose("eithonBungeeJoinEvent", String.format("player=%s", player.getName()));
			String mainGroup = msgin.readUTF();
			verbose("eithonBungeeJoinEvent", String.format("mainGroup=%s", mainGroup));
			if (mainGroup.isEmpty()) mainGroup = null;
			EithonBungeeJoinEvent e = new EithonBungeeJoinEvent(serverName, player, mainGroup);
			Bukkit.getServer().getPluginManager().callEvent(e);
		} catch (IOException e) {
			e.printStackTrace();
			verbose("eithonBungeeJoinEvent", "FAIL and Leave");
			return;
		}
		verbose("eithonBungeeJoinEvent", "Leave");
	}
	
	private void eithonBungeeQuitEvent(byte[] msgbytes) {
		verbose("eithonBungeeQuitEvent", String.format("Enter msgbytes=%s", msgbytes.toString()));
		DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
		try {
			String serverName = msgin.readUTF();
			verbose("eithonBungeeQuitEvent", String.format("serverName=%s", serverName));
			if (serverName.isEmpty()) serverName = null;
			String uuid = msgin.readUTF();
			verbose("eithonBungeeQuitEvent", String.format("player uuid=%s", uuid));
			UUID playerId = UUID.fromString(uuid);
			EithonPlayer player = new EithonPlayer(playerId);
			if (player.getOfflinePlayer() == null) {
				verbose("eithonBungeeQuitEvent", "No user found, Leave");
				return;				
			}
			verbose("eithonBungeeQuitEvent", String.format("player=%s", player.getName()));
			String mainGroup = msgin.readUTF();
			verbose("eithonBungeeQuitEvent", String.format("mainGroup=%s", mainGroup));
			if (mainGroup.isEmpty()) mainGroup = null;
			EithonBungeeQuitEvent e = new EithonBungeeQuitEvent(serverName, player, mainGroup);
			Bukkit.getServer().getPluginManager().callEvent(e);
		} catch (IOException e) {
			e.printStackTrace();
			verbose("eithonBungeeQuitEvent", "FAIL and Leave");
			return;
		}
		verbose("eithonBungeeQuitEvent", "Leave");
	}

	private void verbose(String method, String format, Object... args) {
		String message = CoreMisc.safeFormat(format, args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "BungeeListener.%s: %s", method, message);
	}
}

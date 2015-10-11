package net.eithon.library.bungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class BungeeListener implements PluginMessageListener {

	private EithonPlugin _eithonPlugin;

	public BungeeListener(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
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
		} else if (subchannel.equals("Forward")) {
			forward(in);
		} else if (subchannel.equals("EtihonTest")) {
			short len = in.readShort();
			verbose("onPluginMessageReceived", String.format("len=%d", len));
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			eithonTest(msgbytes);
	}
		verbose("onPluginMessageReceived", "Leave");
	}

	private void getServer(ByteArrayDataInput in, String subchannel) {
		verbose("getServer", "Enter");
		String serverName = in.readUTF();
		verbose("getServer", String.format("serverName=%s", serverName));
		Bukkit.broadcastMessage(String.format("This BungeeCord server name is %s", serverName));
		verbose("getServer", "Leave");
	}

	private void forward(ByteArrayDataInput in) {
		verbose("forward", "Enter");
		String subchannel = in.readUTF();
		verbose("forward", String.format("subchannel=%s", subchannel));
		short len = in.readShort();
		verbose("forward", String.format("len=%d", len));
		byte[] msgbytes = new byte[len];
		in.readFully(msgbytes);
		if (subchannel.equals("EtihonTest")) {
			eithonTest(msgbytes);
		}
		verbose("forward", "Leave");
	}

	private void eithonTest(byte[] msgbytes) {
		verbose("eithonTest", String.format("Enter msgbytes=%s", msgbytes.toString()));
		DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
		try {
			String somedata = msgin.readUTF();
			verbose("eithonTest", String.format("somedata=%s", somedata));
			short somenumber = msgin.readShort();
			verbose("eithonTest", String.format("somenumber=%d", somenumber));
			String broadcastMessage = String.format("%s The magic number is %d", somedata, somenumber);
			verbose("eithonTest", String.format("broadcast \"%s\"", broadcastMessage));
			Bukkit.broadcastMessage(broadcastMessage);
		} catch (IOException e) {
			e.printStackTrace();
			verbose("eithonTest", "FAIL and Leave");
			return;
		}
		verbose("eithonTest", "Leave");
	}

	private void verbose(String method, String format, Object... args) {
		String message = CoreMisc.safeFormat(format, args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "BungeeListener.%s: %s", method, message);
	}
}

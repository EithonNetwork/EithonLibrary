package net.eithon.library.bungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class BungeeListener implements PluginMessageListener {

	public BungeeListener(EithonPlugin eithonPlugin) {
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if (subchannel.equals("GetServer")) {
			getServer(in, subchannel);
		}else if (subchannel.equals("Forward")) {
			forward(in, subchannel);
		}
	}

	private void getServer(ByteArrayDataInput in, String subchannel) {
		String serverName = in.readUTF();
		Bukkit.broadcastMessage(String.format("This BungeeCord server name is %s", serverName));
	}

	private void forward(ByteArrayDataInput in, String subchannel) {
		String subChannel = in.readUTF();
		short len = in.readShort();
		byte[] msgbytes = new byte[len];
		in.readFully(msgbytes);
		if (subchannel.equals("EithonTest")) {
			eithonTest(msgbytes);
		}
	}

	private void eithonTest(byte[] msgbytes) {
		DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
		try {
			String somedata = msgin.readUTF();
			short somenumber = msgin.readShort();
			Bukkit.broadcastMessage(String.format("%s The magic number is %d", somedata, somenumber));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

}

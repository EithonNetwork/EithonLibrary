package net.eithon.library.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BungeeSender {
	private EithonPlugin _eithonPlugin;

	public BungeeSender(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
	}

	private void Send(Player player, String subChannel, ByteArrayOutputStream message, String... arguments) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(subChannel);
		for (String argument : arguments) {
			out.writeUTF(argument);
		}

		if (message != null) {
			out.writeShort(message.toByteArray().length);
			out.write(message.toByteArray());
		}

		player.sendPluginMessage(this._eithonPlugin, "BungeeCord", out.toByteArray());
	}

	private void Send(String subChannel, ByteArrayOutputStream message, String... arguments) {
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		Send(player, subChannel, message, arguments);
	}

	private void Send(String subChannel, String... arguments) {
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		Send(player, subChannel, null, arguments);
	}

	public void Forward() {
		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF("Hello world!");
			msgout.writeShort(4711);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Send("Forward", msgbytes, "All", "EithonTest");
	}

	public void GetServer() {
		Send("GetServer");
	}
}

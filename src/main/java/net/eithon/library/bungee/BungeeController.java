package net.eithon.library.bungee;

import net.eithon.library.extensions.EithonPlugin;

public class BungeeController {

	private BungeeListener _bungeeListener;
	private BungeeSender _bungeeSender;

	public BungeeController(EithonPlugin eithonPlugin) {
		eithonPlugin.getServer().getMessenger().registerOutgoingPluginChannel(eithonPlugin, "BungeeCord");
		this._bungeeListener = new BungeeListener(eithonPlugin);
		eithonPlugin.getServer().getMessenger().registerIncomingPluginChannel(eithonPlugin, "BungeeCord", this._bungeeListener);
		this._bungeeSender = new BungeeSender(eithonPlugin);
	}

	public BungeeSender getBungeeSender() {
		return this._bungeeSender;
	}
}

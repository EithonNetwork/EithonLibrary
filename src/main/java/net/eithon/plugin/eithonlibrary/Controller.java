package net.eithon.plugin.eithonlibrary;

import net.eithon.library.bungee.BungeeController;

public class Controller {
	private BungeeController _bungeeController;

	public Controller(BungeeController bungeeController) {
		this._bungeeController = bungeeController;
	}

	public String getBungeeServerName() {
		return this._bungeeController.getServerName();
	}
}

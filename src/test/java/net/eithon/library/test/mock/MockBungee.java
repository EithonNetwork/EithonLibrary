package net.eithon.library.test.mock;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.bungee.EithonBungeeEvent;
import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class MockBungee {
	private BungeeController _central;
	private BungeeController _remote;
	public static String CENTRAL_SERVER_NAME = "central";
	public static String REMOTE_SERVER_NAME = "remote";
	private MockMinecraft _mockCentralServer;
	private MockEithonLibrary _mockCentralEithonLibrary;
	private MockMinecraft _mockRemoteServer;
	private MockEithonLibrary _mockRemoteEithonLibrary;

	public MockBungee() {
		this._mockCentralServer = new MockMinecraft(CENTRAL_SERVER_NAME);
		this._mockCentralEithonLibrary = new MockEithonLibrary(this._mockCentralServer.getServer());

		this._mockRemoteServer = new MockMinecraft(REMOTE_SERVER_NAME);
		this._mockRemoteEithonLibrary = new MockEithonLibrary(this._mockRemoteServer.getServer());

		// Create the central BungeeController
		this._central = new BungeeController(this._mockCentralEithonLibrary.getEithonPlugin());

		// Create the remote BungeeController
		this._remote = new BungeeController(this._mockRemoteEithonLibrary.getEithonPlugin());

		// Connect them
		this._mockCentralServer.mockBungee(this._central, this._remote);
		this._mockRemoteServer.mockBungee(this._remote, this._central);

		// Initialize them
		this._central.initialize();
		this._remote.initialize();

	}

	public void addBungeeEventListener(IEithonBungeeEventListener listener) {
		this._mockCentralServer.addBungeeEventListener(listener);
	}

	public BungeeController getCentralBungeeController() { return this._central; }

	public BungeeController getRemoteBungeeController() { return this._remote; }

	public EithonPlugin getCentralEithonPlugin() { return this._mockCentralEithonLibrary.getEithonPlugin(); }

	public EithonPlugin getRemoteEithonPlugin() { return this._mockRemoteEithonLibrary.getEithonPlugin(); }
	
	public Player getCentralPlayer() { return this._mockCentralServer.getPlayer(); } 

	public void verify() {
		this._mockCentralServer.verify();
		this._mockCentralEithonLibrary.verify();
		this._mockRemoteServer.verify();
		this._mockRemoteEithonLibrary.verify();
	}
}

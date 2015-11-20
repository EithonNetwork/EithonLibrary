package net.eithon.library.test.mock;

import java.util.ArrayList;
import java.util.Collection;

import net.eithon.library.bungee.BungeeController;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.internal.invocationcontrol.EasyMockMethodInvocationControl;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({Bukkit.class})
public class MockMinecraft {
	private Messenger _messenger;
	private Player _player;
	private Server _server;

	public MockMinecraft() {
		// Create a mock messenger
		this._messenger = EasyMock.createNiceMock(Messenger.class);
		PowerMock.replay(this._messenger);

		// Create a mock Server
		this._server = EasyMock.createNiceMock(Server.class);
		EasyMock.expect(this._server.getMessenger()).andReturn(this._messenger).anyTimes();
		EasyMock.replay(this._server);

		// Create a mock player
		final Player mockPlayer = EasyMock.createNiceMock(Player.class);
		this._player = mockPlayer;
		Capture<byte[]> capturedMessage = new Capture<byte[]>();
		this._player.sendPluginMessage(
				EasyMock.anyObject(), 
				EasyMock.eq("BungeeCord"),
				EasyMock.capture(capturedMessage));
		EasyMock.expectLastCall()
		.andAnswer(new IAnswer<Object>() {
			public Object answer() {
				BungeeController.simulateBungee(mockPlayer, capturedMessage.getValue());
				//return the value to be returned by the method (null for void)
				return null;
			}
		})
		.anyTimes();
		EasyMock.replay(this._player);
		ArrayList<Player> playerCollection = new ArrayList<Player>();
		playerCollection.add(this._player);

		// Create a mock Bukkit
		PowerMock.mockStatic(Bukkit.class);
		EasyMock.expect(Bukkit.getOnlinePlayers()).andReturn((Collection)playerCollection);
		PowerMock.replay(Bukkit.class);
	}

	public void verify() {
		PowerMock.verify(this._messenger);
		EasyMock.verify(this._server);
		PowerMock.verify(this._player);
		PowerMock.verify(Bukkit.class);
	}

	public Server getServer() { return this._server; }
}

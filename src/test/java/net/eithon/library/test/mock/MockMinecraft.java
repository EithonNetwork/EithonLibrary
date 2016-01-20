package net.eithon.library.test.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.bungee.EithonBungeeEvent;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.Messenger;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({Bukkit.class})
public class MockMinecraft {
	private Messenger _messenger;
	private Player _player;
	private Server _server;
	private Class<Bukkit> _bukkitClass;
	private PluginManager _pluginManager;
	private ArrayList<Object> _eithonBungeeEventListeners;

	public MockMinecraft(String serverName) {
		this._eithonBungeeEventListeners = new ArrayList<Object>();
		// Create a mock messenger
		this._messenger = EasyMock.createNiceMock(Messenger.class);
		PowerMock.replay(this._messenger);

		// Create a mock messenger
		this._pluginManager = EasyMock.createNiceMock(PluginManager.class);
		Capture<Event> capturedEvent = new Capture<Event>();
		this._pluginManager.callEvent(EasyMock.capture(capturedEvent));
		EasyMock.expectLastCall()
		.andAnswer(new IAnswer<Object>() {
			public Object answer() {
				onEvent(capturedEvent.getValue());
				return null;
			}
		})
		.anyTimes();
		EasyMock.replay(this._pluginManager);

		// Create a mock Server
		this._server = EasyMock.createNiceMock(Server.class);
		EasyMock.expect(this._server.getMessenger()).andReturn(this._messenger).anyTimes();
		EasyMock.expect(this._server.getServerName()).andReturn(serverName).anyTimes();
		EasyMock.expect(this._server.getPluginManager()).andReturn(this._pluginManager).anyTimes();
		EasyMock.replay(this._server);
	}

	protected void onEvent(Event event) {
		if (event instanceof EithonBungeeEvent) {
			EithonBungeeEvent ebe = (EithonBungeeEvent) event;
			for (Object object : this._eithonBungeeEventListeners) {
				IEithonBungeeEventListener listener = (IEithonBungeeEventListener) object;
				listener.onBungeeEvent(ebe);
			}
		}
	}

	public void addBungeeEventListener(IEithonBungeeEventListener listener) {
		this._eithonBungeeEventListeners.add(listener);
	}

	public void mockBungee(final BungeeController senderServer, final BungeeController receiverServer) {

		// Debug purpose
		final String senderServerName = senderServer.getMinecraftServerName();
		// Create a mock player
		final Player mockPlayer = EasyMock.createNiceMock(Player.class);
		this._player = mockPlayer;
		UUID uuid = UUID.randomUUID();
		EasyMock.expect(this._player.getUniqueId()).andReturn(uuid).anyTimes();
		Capture<byte[]> capturedMessage = new Capture<byte[]>();
		/* expect */ this._player.sendPluginMessage(
				EasyMock.anyObject(), 
				EasyMock.eq("BungeeCord"),
				EasyMock.capture(capturedMessage));
		EasyMock.expectLastCall()
		.andAnswer(new IAnswer<Object>() {
			public Object answer() {
				String a = senderServerName;
				senderServer.simulateSendPluginMessage(receiverServer, mockPlayer, capturedMessage.getValue());
				//return the value to be returned by the method (null for void)
				return a;
			}
		})
		.anyTimes();
		EasyMock.replay(this._player);
		ArrayList<Player> playerCollection = new ArrayList<Player>();
		playerCollection.add(this._player);

		// Create a mock Bukkit
		this._bukkitClass = Bukkit.class;
		PowerMock.mockStatic(this._bukkitClass);
		EasyMock.expect(Bukkit.getPlayer((UUID)EasyMock.anyObject())).andReturn(mockPlayer);
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(Bukkit.getOnlinePlayers()).andReturn((Collection)playerCollection);
		EasyMock.expectLastCall().anyTimes();
		PowerMock.replay(this._bukkitClass);
	}

	public void verify() {
		PowerMock.verify(this._messenger);
		EasyMock.verify(this._server);
		if (this._player != null) PowerMock.verify(this._player);
		if (this._bukkitClass != null) PowerMock.verify(this._bukkitClass);
	}

	public Server getServer() { return this._server; }

	public Player getPlayer() { return this._player; }
}
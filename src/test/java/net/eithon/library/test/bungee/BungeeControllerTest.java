package net.eithon.library.test.bungee;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EithonPlugin.class, Bukkit.class})
public class BungeeControllerTest {
	@Test
	public void testInit() 
	{
		// Create a mock messenger
		Messenger messengerMock = EasyMock.createNiceMock(Messenger.class);
		
		// Create a mock player
		Player mockPlayer = EasyMock.createNiceMock(Player.class);
		ArrayList<Player> playerCollection = new ArrayList<Player>();
		playerCollection.add(mockPlayer);
		
		// Create a mock Bukkit
		PowerMock.mockStatic(Bukkit.class);
		EasyMock.expect(Bukkit.getOnlinePlayers()).andReturn((Collection)playerCollection);
		PowerMock.replay(Bukkit.class);
		
		// Create a mock Server
		Server mockServer = EasyMock.createNiceMock(Server.class);
		EasyMock.expect(mockServer.getMessenger()).andReturn(messengerMock);
		EasyMock.expect(mockServer.getMessenger()).andReturn(messengerMock);
		EasyMock.replay(mockServer);
		
		// Create a mock logger
		Logger mockLogger = EasyMock.createNiceMock(Logger.class);
		EasyMock.replay(mockLogger);
		
		// Create the mock player
		EithonPlugin mockEithonPlugin = PowerMock.createNiceMock(EithonPlugin.class);
		EasyMock.expect(mockEithonPlugin.getServer()).andReturn(mockServer);
		EasyMock.expect(mockEithonPlugin.getServer()).andReturn(mockServer);
		PowerMock.replay(mockEithonPlugin);

		BungeeController controller = new BungeeController(mockEithonPlugin);

		// Verify mocks.
		EasyMock.verify(mockEithonPlugin);
		EasyMock.verify(mockServer);
	}
}

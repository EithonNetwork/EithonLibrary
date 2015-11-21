package net.eithon.library.test.bungee;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.test.mock.MockEithonLibrary;
import net.eithon.library.test.mock.MockMinecraft;

import org.bukkit.Bukkit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EithonPlugin.class, Bukkit.class})
public class BungeeControllerTest {
	@Test
	public void testBungeeControllerConstructor() 
	{
		MockMinecraft mockCentralServer = new MockMinecraft("central");
		MockEithonLibrary mockCentralEithonLibrary = new MockEithonLibrary(mockCentralServer.getServer());
		
		// Create the BungeeController
		BungeeController central = new BungeeController(mockCentralEithonLibrary.getEithonPlugin());
		mockCentralServer.mockBungee(central,  central);
		
		// Prepare server name
		central.initialize();
		
		mockCentralServer.verify();
		mockCentralEithonLibrary.verify();
	}
	
	@Test
	public void testGetServerName() 
	{
		MockMinecraft mockCentralServer = new MockMinecraft("central");
		MockEithonLibrary mockCentralEithonLibrary = new MockEithonLibrary(mockCentralServer.getServer());
		
		// Create and initialize the BungeeController
		BungeeController central = new BungeeController(mockCentralEithonLibrary.getEithonPlugin());
		
		// Mock for bungee
		mockCentralServer.mockBungee(central, central);
		
		// Prepare server name
		central.initialize();
		
		// Get server name
		String serverName = central.getServerName();
		Assert.assertEquals("central", serverName);
		
		mockCentralServer.verify();
		mockCentralEithonLibrary.verify();
	}
	
	@Test
	public void testForward() 
	{
		MockMinecraft mockCentralServer = new MockMinecraft("central");
		MockEithonLibrary mockCentralEithonLibrary = new MockEithonLibrary(mockCentralServer.getServer());
		
		MockMinecraft mockRemoteServer = new MockMinecraft("remote");
		MockEithonLibrary mockRemoteEithonLibrary = new MockEithonLibrary(mockRemoteServer.getServer());
		
		// Create the central BungeeController
		BungeeController central = new BungeeController(mockCentralEithonLibrary.getEithonPlugin());
		
		// Create the remote BungeeController
		BungeeController remote = new BungeeController(mockRemoteEithonLibrary.getEithonPlugin());

		// Connect them
		mockCentralServer.mockBungee(central, remote);
		mockRemoteServer.mockBungee(remote, central);
		
		EithonPlayer eithonPlayer = new EithonPlayer(mockCentralServer.getPlayer());
		
		PlayerStatistics playerStatistics = new PlayerStatistics(eithonPlayer);
		PlayerStatistics.initialize(mockCentralEithonLibrary.getLogger());
		central.sendDataToServer("remote", "Test", playerStatistics, false);
		
		mockCentralServer.verify();
		mockCentralEithonLibrary.verify();
	}
}

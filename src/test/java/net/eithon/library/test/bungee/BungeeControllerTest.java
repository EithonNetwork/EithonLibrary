package net.eithon.library.test.bungee;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.bungee.EithonBungeeEvent;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.test.mock.IEithonBungeeEventListener;
import net.eithon.library.test.mock.MockBungee;
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
		MockBungee mockBungee = new MockBungee();

		// Do the test

		PlayerStatistics playerStatistics = new PlayerStatistics(mockBungee.getCentralPlayer());
		PlayerStatistics.initialize(mockBungee.getCentralEithonPlugin().getEithonLogger());
		mockBungee.addBungeeEventListener(new IEithonBungeeEventListener() {
			@Override
			public void onBungeeEvent(EithonBungeeEvent event) {
				testForwardRemoteEvent(event);
			}
		});
		mockBungee.getCentralBungeeController()
		.sendDataToServer("remote", "Test", playerStatistics, false);

		mockBungee.verify();
	}

	void testForwardRemoteEvent(EithonBungeeEvent event) {
		if (event.getName().equalsIgnoreCase("test")) {
			PlayerStatistics playerStatistics = PlayerStatistics.getFromJson(event.getData());
			String sourceServer = event.getSourceServerName();
			String a = sourceServer + playerStatistics.toJsonString();
		}
	}
}

package net.eithon.library.test.bungee;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.bungee.EithonBungeeEvent;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.test.mock.IEithonBungeeEventListener;
import net.eithon.library.test.mock.MockBungee;
import net.eithon.library.test.mock.MockEithonLibrary;
import net.eithon.library.test.mock.MockMinecraft;

import org.bukkit.Bukkit;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EithonPlugin.class, Bukkit.class})
public class BungeeControllerTest {
	@Test
	public void understandEasyMock() 
	{
		PlayerStatistics s1 = EasyMock.createMock(PlayerStatistics.class);
		EasyMock.expect(s1.getTotalTimeInSeconds()).andReturn((long) 1).anyTimes();
		EasyMock.replay(s1);
		
		PlayerStatistics s2 = EasyMock.createMock(PlayerStatistics.class);
		EasyMock.expect(s2.getTotalTimeInSeconds()).andReturn((long) 2).anyTimes();
		EasyMock.replay(s2);
		
		Assert.assertEquals(1, s1.getTotalTimeInSeconds());
		Assert.assertEquals(2, s2.getTotalTimeInSeconds());
		Assert.assertEquals(1, s1.getTotalTimeInSeconds());
		Assert.assertEquals(2, s2.getTotalTimeInSeconds());		
	}
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

		// Verify that the mock works
		Assert.assertEquals("central", mockBungee.getCentralEithonPlugin().getServer().getServerName());
		Assert.assertEquals("remote", mockBungee.getRemoteEithonPlugin().getServer().getServerName());
		
		// Do the test
		PlayerStatistics playerStatistics = new PlayerStatistics(mockBungee.getCentralPlayer());
		PlayerStatistics.initialize(mockBungee.getCentralEithonPlugin().getEithonLogger());
		mockBungee.addBungeeEventListener(new IEithonBungeeEventListener() {
			@Override
			public void onBungeeEvent(EithonBungeeEvent event) {
				Assert.assertEquals("Test", event.getName());
				assertEquals(playerStatistics, PlayerStatistics.getFromJson(event.getData()));
				Assert.assertEquals(mockBungee.getCentralEithonPlugin().getServer().getServerName(), event.getSourceServerName());
			}
		});
		mockBungee.getCentralBungeeController()
		.sendDataToServer("remote", "Test", playerStatistics, false);

		mockBungee.verify();
	}

	void assertEquals(PlayerStatistics expected,
			PlayerStatistics actual) {
		Assert.assertEquals(expected.getBlocksCreated(), actual.getBlocksCreated());
		Assert.assertEquals(expected.getAfkDescription(), actual.getAfkDescription());
	}
}

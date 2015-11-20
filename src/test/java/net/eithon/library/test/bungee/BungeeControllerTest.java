package net.eithon.library.test.bungee;

import net.eithon.library.bungee.BungeeController;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.test.mock.MockEithonLibrary;
import net.eithon.library.test.mock.MockMinecraft;

import org.bukkit.Bukkit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EithonPlugin.class, Bukkit.class})
public class BungeeControllerTest {
	@Test
	public void testBungeeControllerInit() 
	{
		MockMinecraft mockMinecraft = new MockMinecraft();
		MockEithonLibrary mockEithonLibrary = new MockEithonLibrary(mockMinecraft.getServer());
		
		// Initialize the BungeeController

		BungeeController controller = new BungeeController(mockEithonLibrary.getEithonPlugin());
		
		mockMinecraft.verify();
		mockEithonLibrary.verify();
	}
	
	@Test
	public void testGetServerName() 
	{
		MockMinecraft mockMinecraft = new MockMinecraft();
		MockEithonLibrary mockEithonLibrary = new MockEithonLibrary(mockMinecraft.getServer());
		
		// Initialize the BungeeController

		BungeeController controller = new BungeeController(mockEithonLibrary.getEithonPlugin());
		controller.getServerName();
		
		mockMinecraft.verify();
		mockEithonLibrary.verify();
	}
}

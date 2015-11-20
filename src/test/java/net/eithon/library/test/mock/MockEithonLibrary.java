package net.eithon.library.test.mock;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger;

import org.bukkit.Server;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({Logger.class})
public class MockEithonLibrary {
	private Logger _logger;
	private EithonPlugin _eithonPlugin;

	public MockEithonLibrary(Server server) {
		// Create a mock logger
		this._logger = EasyMock.createNiceMock(Logger.class);
		EasyMock.replay(this._logger);
		
		// Create a mock EithonPlugin
		this._eithonPlugin = PowerMock.createNiceMock(EithonPlugin.class);
		EasyMock.expect(this._eithonPlugin.getServer()).andReturn(server).anyTimes();
		EasyMock.expect(this._eithonPlugin.getEithonLogger()).andReturn(this._logger).anyTimes();
		PowerMock.replay(this._eithonPlugin);
	}
	
	public void verify() {
		EasyMock.verify(this._logger);
		EasyMock.verify(this._eithonPlugin);
	}

	public EithonPlugin getEithonPlugin() { return this._eithonPlugin; }

	public Logger getLogger() { return this._logger; }
}

package net.eithon.plugin.eithonlibrary;

public class EithonLibraryApi {
	private Controller _controller;

	EithonLibraryApi(Controller controller) {
		this._controller = controller;
	}
	
	public String getBungeeServerName() {
		return this._controller.getBungeeServerName();
	}
}

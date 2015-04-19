package net.eithon.library.plugin;

import net.eithon.library.extensions.EithonPlugin;

public class GeneralMessage {

	public static ConfigurableMessage expectedToBePlayer;
	public static ConfigurableMessage requiredPermission;
	
	public static void initialize(EithonPlugin plugin) {
		expectedToBePlayer = plugin.getConfigurableMessage("messages.ExpectedToBePlayer", 1,
				"Expected %s to be a Player.");
		requiredPermission = plugin.getConfigurableMessage("messages.RequiredPermission", 1,
				"You must have permission %s.");
	}
}

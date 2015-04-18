package net.eithon.library.plugin;

import net.eithon.library.extensions.EithonPlugin;

public class GeneralMessage {

	public static ConfigurableMessage requiredPermission;
	
	public static void initialize(EithonPlugin plugin) {
		requiredPermission = plugin.getConfigurableMessage("messages.RequiredPermission", 1,
				"You must have permission %s.");
	}
}

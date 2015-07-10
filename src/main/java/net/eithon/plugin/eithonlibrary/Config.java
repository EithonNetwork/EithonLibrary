package net.eithon.plugin.eithonlibrary;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.ConfigurableCommand;
import net.eithon.library.plugin.ConfigurableMessage;
import net.eithon.library.plugin.Configuration;

public class Config {
	public static void load(EithonPlugin plugin)
	{
		Configuration config = plugin.getConfiguration();
		V.load(config);
		C.load(config);
		M.load(config);

	}
	public static class V {
		public static int eithonDebugLevel;

		static void load(Configuration config) {
			eithonDebugLevel = config.getInt("eithon.DebugLevel", 4);
		}
	}
	public static class C {

		public static ConfigurableCommand addGroupCommand;
		public static ConfigurableCommand removeGroupCommand;

		static void load(Configuration config) {
			addGroupCommand = config.getConfigurableCommand("commands.AddGroup_2", 2,
					"perm player %s addgroup %s");
			removeGroupCommand = config.getConfigurableCommand("commands.RemoveGroup_2", 2,
					"perm player %s removegroup %s");
		}
	}
	
	public static class M {
		public static ConfigurableMessage expectedToBePlayer;
		public static ConfigurableMessage requiredPermission;
		public static ConfigurableMessage expectedWorlds;

		static void load(Configuration config) {
			expectedToBePlayer = config.getConfigurableMessage("messages.ExpectedToBePlayer", 1,
					"Expected %s to be a Player.");
			requiredPermission = config.getConfigurableMessage("messages.RequiredPermission", 1,
					"You must have permission %s.");
			expectedWorlds = config.getConfigurableMessage("messages.ExpectedWorlds_2", 2,
					"You are in world %s, but was expected to be in one of the following worlds: [%s].");
		}		
	}

}
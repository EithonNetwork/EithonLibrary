package net.eithon.plugin.eithonlibrary;

import java.util.List;

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
		public static int titleFadeInTicks;
		public static int titleStayTicks;
		public static int titleFadeOutTicks;
		public static List<String> groupPriorities;
		public static String primaryBungeeServer;
		

		static void load(Configuration config) {
			eithonDebugLevel = config.getInt("eithon.DebugLevel", 4);
			titleFadeInTicks = config.getInt("eithon.TitleFadeInTicks", 20);
			titleStayTicks = config.getInt("eithon.TitleStayTicks", 60);
			titleFadeOutTicks = config.getInt("eithon.TitleFadeOutTicks", 20);
			groupPriorities = config.getStringList("GroupPriorities");
			primaryBungeeServer = config.getString("PrimaryBungeeServer", "Main");
		}
	}
	public static class C {
		static void load(Configuration config) {
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

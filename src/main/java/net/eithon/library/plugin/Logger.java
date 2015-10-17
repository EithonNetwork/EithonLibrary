package net.eithon.library.plugin;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlugin;

public class Logger {
	private static Logger defaultDebug = null;
	private static DebugPrintLevel[] debugLevelValues = null;
	private EithonPlugin _plugin = null;
	private DebugPrintLevel _debugLevel = DebugPrintLevel.NONE;
	private int _debugLevelAsInt = 0;

	public enum DebugPrintLevel {
		NONE, MAJOR, MINOR, VERBOSE
	}
	
	public static void initialize() {
		debugLevelValues = DebugPrintLevel.values();
	}
	
	public static void setDefaultDebug(Logger debug) {
		defaultDebug = debug;
	}
	
	public Logger(EithonPlugin plugin) { 
		this._plugin = plugin; 
	}
	
	public void enable() {
		this._debugLevelAsInt = this._plugin.getConfiguration().getInt("eithon.DebugLevel", 3);
		this._debugLevel = fromInt(this._debugLevelAsInt);
	}
	
	public void setDebugLevel(DebugPrintLevel level) {
		this._debugLevel = level;
		this._debugLevelAsInt = toInt(this._debugLevel);
	}
	
	public void setDebugLevel(int level) {
		this._debugLevelAsInt = level;
		this._debugLevel = fromInt(this._debugLevelAsInt);
	}

	public static void consolePrintF(String format, Object... args) {
		String message = CoreMisc.safeFormat(format, args);
		consolePrint(message);
	}

	public static void consolePrint(String message) {
		if (System.console() != null) System.console().printf("%s", message);
	}

	public static void libraryDebug(DebugPrintLevel level, String format, Object... args) 
	{
		if (defaultDebug == null) {
			consolePrintF(format, args);
			return;
		}
		defaultDebug.debug(level, format, args);
	}

	public void debug(DebugPrintLevel level, String format, Object... args) 
	{
		if (!shouldDebug(level)) return;
		String message = formatMessage("debug", format, args);
		try {
			this._plugin.getLogger().info(message);
		} catch (Exception e) {
			consolePrint(message);
		}
	}

	public static void libraryInfo(String format, Object... args) 
	{
		if (defaultDebug == null) {
			consolePrintF(format, args);
			return;
		}
		defaultDebug.info(format, args);
	}

	public void info(String format, Object... args) 
	{
		String message = formatMessage("info", format, args);
		try {
			this._plugin.getLogger().info(message);
		} catch (Exception e) {
			consolePrint(message);
		}
	}

	public static void libraryWarning(String format, Object... args) 
	{
		if (defaultDebug == null) {
			consolePrintF(format, args);
			return;
		}
		defaultDebug.warning(format, args);
	}

	public void warning(String format, Object... args) 
	{
		String message = formatMessage("warning", format, args);
		try {
			this._plugin.getLogger().warning(message);
		} catch (Exception e) {
			consolePrint(message);
		}
	}

	public static void libraryError(String format, Object... args) 
	{
		if (defaultDebug == null) {
			consolePrintF(format, args);
			return;
		}
		defaultDebug.error(format, args);
	}


	public void error(String format, Object... args) 
	{
		String message = formatMessage("error", format, args);
		try {
			this._plugin.getLogger().severe(message);
		} catch (Exception e) {
			consolePrint(message);
		}
	}
	
	private int toInt(DebugPrintLevel level) {
		for (int i = 0; i < debugLevelValues.length; i++) {
			if (debugLevelValues[i] == level) return i;
		}
		warning("Did not recognize a debug level.");
		return 0;
	}
	
	public DebugPrintLevel fromInt(int level) {
		try {
		return debugLevelValues[level];
		} catch (Exception e) {
			warning("Unknown debug level (%d). Debug level was set to %d (VERBOSE).", level, 3);
			return DebugPrintLevel.VERBOSE;
		}
	}

	private String formatMessage(String type, String format, Object... args) {
		return String.format("(%s) %s", 
				type,
				CoreMisc.safeFormat(format, args));
	}

	public boolean shouldDebug(DebugPrintLevel level) {
		return this._debugLevelAsInt >= toInt(level);
	}
}

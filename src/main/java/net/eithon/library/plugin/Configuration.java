package net.eithon.library.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.file.FileMisc;
import net.eithon.library.misc.Debug.DebugPrintLevel;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {
	private File _configFile;
	private FileConfiguration _config;
	private EithonPlugin _plugin;

	public Configuration(EithonPlugin plugin)
	{
		this._plugin = plugin;
	}

	public void enable() {
		this._configFile = initializeConfigFile(this._plugin.getJavaPlugin(), "config.yml");
		this._config = new YamlConfiguration();
		load();
	}

	public ConfigurableMessage getConfigurableMessage(String path, int parameters, String defaultValue) {
		return new ConfigurableMessage(this, path, parameters, defaultValue);
	}

	public ConfigurableCommand getConfigurableCommand(String path, int parameters, String defaultValue) {
		return new ConfigurableCommand(this, path, parameters, defaultValue);
	}

	public String getString(String path, String defaultValue)
	{
		String result;
		try {
			result = this._config.getString(path, defaultValue);
		} catch (Exception ex) {
			this._plugin.getDebug().warning("Failed to read configuration \"%s\", will use default value (%s).",
					path, defaultValue);
			this._plugin.getDebug().debug(DebugPrintLevel.MAJOR, "Exception: %s", ex.getMessage());
			result = defaultValue;
		}
		this._plugin.getDebug().debug(DebugPrintLevel.MINOR, "Configuration \"%s\" = %s" , path, result);
		return result;
	}

	public double getDouble(String path, double defaultValue)
	{
		double value;
		try {
			value = this._config.getDouble(path, defaultValue);
		} catch (Exception ex) {
			this._plugin.getDebug().warning("Failed to read configuration \"%s\", will use default value (%.2f).",
					path, defaultValue);
			this._plugin.getDebug().debug(DebugPrintLevel.MAJOR, "Exception: %s", ex.getMessage());
			value = defaultValue;
		}
		this._plugin.getDebug().debug(DebugPrintLevel.MINOR, "Configuration \"%s\" = %.2f" , path, value);
		return value;
	}

	public int getInt(String path, int defaultValue)
	{
		int value;
		try {
			value = this._config.getInt(path, defaultValue);
		} catch (Exception ex) {
			this._plugin.getDebug().warning("Failed to read configuration \"%s\", will use default value (%d).",
					path, defaultValue);
			this._plugin.getDebug().debug(DebugPrintLevel.MAJOR, "Exception: %s", ex.getMessage());
			value = defaultValue;
		}
		this._plugin.getDebug().debug(DebugPrintLevel.MINOR, "Configuration \"%s\" = %d" , path, value);
		return value;
	}

	public List<Integer> getIntegerList(String path)
	{
		List<Integer> result = null;
		try {
			result = this._config.getIntegerList(path);
		} catch (Exception ex) {
			this._plugin.getDebug().warning("Failed to read configuration \"%s\".", path);
			this._plugin.getDebug().debug(DebugPrintLevel.MAJOR, "Exception: %s", ex.getMessage());
			result = new ArrayList<Integer>();
		}

		if (this._plugin.getDebug().shouldDebug(DebugPrintLevel.MINOR)) {
			String s = "";
			boolean first = true;
			for (Integer integer : result) {
				if (first) first = false;
				else s += ", ";
				s += integer.toString();
			}
			this._plugin.getDebug().debug(DebugPrintLevel.MINOR, "Configuration \"%s\" = [%s]" , path, s);
		}
		return result;
	}
	
	public List<String> getStringList(String path)
	{
		List<String> result = null;
		try {
			result = this._config.getStringList(path);
		} catch (Exception ex) {
			this._plugin.getDebug().warning("Failed to read configuration \"%s\".", path);
			this._plugin.getDebug().debug(DebugPrintLevel.MAJOR, "Exception: %s", ex.getMessage());
			result = new ArrayList<String>();
		}
		
		if (this._plugin.getDebug().shouldDebug(DebugPrintLevel.MINOR)) {
			String s = "";
			boolean first = true;
			for (String string : result) {
				if (first) first = false;
				else s += ", ";
				s += string;
			}
			this._plugin.getDebug().debug(DebugPrintLevel.MINOR, "Configuration \"%s\" = [%s]" , path, s);
		}
		return result;
	}

	private File initializeConfigFile(JavaPlugin plugin, String fileName) {
		File file = new File(plugin.getDataFolder(), fileName);
		FileMisc.makeSureParentDirectoryExists(file);
		if(!file.exists()) {
			copy(plugin.getResource(fileName), file);
		}

		return file;
	}

	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while((len=in.read(buf))>0){
				out.write(buf,0,len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void load()
	{
		try {
			this._config.load(this._configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

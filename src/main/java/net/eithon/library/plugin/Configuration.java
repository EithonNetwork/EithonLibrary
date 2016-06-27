package net.eithon.library.plugin;

import java.io.File;
import java.util.Map.Entry;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.library.time.TimeMisc;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {
	private File _serverSpecificConfigFile;
	private File _commonConfigFile;
	private FileConfiguration _serverSpecificConfig;
	private FileConfiguration _commonConfig;
	private EithonPlugin _plugin;

	public Configuration(EithonPlugin plugin)
	{
		this._plugin = plugin;
	}

	public void enable() {
		this._commonConfigFile = initializeConfigFile(this._plugin, "config.yml");
		this._commonConfig = new YamlConfiguration();
		this._serverSpecificConfigFile = initializeConfigFile(this._plugin, "server_specific_config.yml");
		this._serverSpecificConfig = new YamlConfiguration();
		load();
	}

	public ConfigurableMessage getConfigurableMessage(String path, int parameters, String defaultValue, String... parameterNames) {
		return new ConfigurableMessage(this._plugin, path, parameters, defaultValue, parameterNames);
	}

	public ConfigurableMessage getConfigurableMessage(String path, int parameters, String defaultValue) {
		return new ConfigurableMessage(this._plugin, path, parameters, defaultValue);
	}

	public ConfigurableCommand getConfigurableCommand(String path, int parameters, String defaultValue) {
		return new ConfigurableCommand(this._plugin, path, parameters, defaultValue);
	}

	public Map<String, Object> getMap(String path, boolean returnNestedMaps)
	{
		ConfigurationSection section = this._serverSpecificConfig.getConfigurationSection(path);
		if (section == null) {
			section = this._commonConfig.getConfigurationSection(path);
			if (section == null) {
				this._plugin.logWarn("Configuration \"%s\" was null", path);
				return null;
			}
		}
		Map<String, Object> values = section.getValues(returnNestedMaps);	
		this._plugin.dbgMinor( "Configuration \"%s\" is a map with %d values" , path, values.size());
		if (this._plugin.getEithonLogger().shouldDebug(DebugPrintLevel.VERBOSE)) debugVerboseMap(values, "  ");
		return values;
	}

	public Object getObject(String path, Object defaultValue)
	{
		Object result;
		try {
			result = this._serverSpecificConfig.get(path, defaultValue);
		} catch (Exception ex) {
			this._plugin.logWarn("Failed to read configuration \"%s\", will use default value (%s).",
					path, defaultValue == null ? "null" : defaultValue.toString());
			this._plugin.dbgMajor( "Exception: %s", ex.getMessage());
			result = defaultValue;
		}
		this._plugin.dbgMinor( "Configuration \"%s\" = %s" ,
				path, result == null ? "null" : result.toString());
		return result;
	}

	@SuppressWarnings("unchecked")
	private void debugVerboseMap(Map<String, Object> values, String indentation) {
		for (Entry<String, Object> entry : values.entrySet()) {
			Object value = entry.getValue();
			String asString = value == null ? "null" : value.toString();
			this._plugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "%s%s = %s" , indentation, entry.getKey(), asString);
			if (value instanceof Map<?, ?>) debugVerboseMap((Map<String, Object>)value, String.format("%s  ", indentation));
		}
	}

	public void setObject(String path, ConfigurationSerializable object) {
		ConfigurationSection section = this._serverSpecificConfig.getConfigurationSection(path);
		if (section == null) section = this._serverSpecificConfig.createSection(path);
		this._serverSpecificConfig.set(path, object);
	}

	public String getString(String path, String defaultValue)
	{
		String result = getStringInternal(path, defaultValue);
		this._plugin.dbgMinor( "Configuration \"%s\" = %s" , path, result);
		return result;
	}

	private String getStringInternal(String path, String defaultValue) {
		try {
			if (this._serverSpecificConfig.contains(path)) {
				return this._serverSpecificConfig.getString(path, defaultValue);
			} else {
				return this._commonConfig.getString(path, defaultValue);
			}
		} catch (Exception ex) {
			this._plugin.logWarn("Failed to read configuration \"%s\", will use default value (%s).",
					path, defaultValue);
			this._plugin.dbgMajor( "Exception: %s", ex.getMessage());
			return defaultValue;
		}
	}

	public double getDouble(String path, double defaultValue)
	{
		double value;
		try {
			if (this._serverSpecificConfig.contains(path)) {
				value = this._serverSpecificConfig.getDouble(path, defaultValue);
			} else {
				value = this._commonConfig.getDouble(path, defaultValue);
			}
		} catch (Exception ex) {
			this._plugin.logWarn("Failed to read configuration \"%s\", will use default value (%.2f).",
					path, defaultValue);
			this._plugin.dbgMajor( "Exception: %s", ex.getMessage());
			value = defaultValue;
		}
		this._plugin.dbgMinor( "Configuration \"%s\" = %.2f" , path, value);
		return value;
	}

	public boolean getBoolean(String path, boolean defaultValue)
	{
		boolean value;
		try {
			if (this._serverSpecificConfig.contains(path)) {
				value = this._serverSpecificConfig.getBoolean(path, defaultValue);
			} else {
				value = this._commonConfig.getBoolean(path, defaultValue);
			}
		} catch (Exception ex) {
			this._plugin.logWarn("Failed to read configuration \"%s\", will use default value (%s).",
					path, defaultValue?"true":"false");
			this._plugin.dbgMajor( "Exception: %s", ex.getMessage());
			value = defaultValue;
		}
		this._plugin.dbgMinor( "Configuration \"%s\" = %s" , path, value?"true":"false");
		return value;
	}

	public int getInt(String path, int defaultValue)
	{
		int value;
		try {
			if (this._serverSpecificConfig.contains(path)) {
				value = this._serverSpecificConfig.getInt(path, defaultValue);
			} else {
				value = this._commonConfig.getInt(path, defaultValue);
			}
		} catch (Exception ex) {
			this._plugin.logWarn("Failed to read configuration \"%s\", will use default value (%d).",
					path, defaultValue);
			this._plugin.dbgMajor( "Exception: %s", ex.getMessage());
			value = defaultValue;
		}
		this._plugin.dbgMinor( "Configuration \"%s\" = %d" , path, value);
		return value;
	}

	public long getSeconds(String path, long defaultValue)
	{
		String valueAsString = getStringInternal(path, String.format("%d", defaultValue));
		long value = TimeMisc.stringToSeconds(valueAsString);
		this._plugin.dbgMinor( "Configuration \"%s\" = %d seconds" , path, value);
		return value;
	}

	public long getSeconds(String path, String defaultValue)
	{
		String valueAsString = getStringInternal(path, defaultValue);
		long value = TimeMisc.stringToSeconds(valueAsString);
		this._plugin.dbgMinor( "Configuration \"%s\" = %d seconds" , path, value);
		return value;
	}

	public long getTicks(String path, long defaultValue)
	{
		String valueAsString = getStringInternal(path, String.format("%d", defaultValue));
		long value = TimeMisc.stringToTicks(valueAsString);
		this._plugin.dbgMinor( "Configuration \"%s\" = %d ticks" , path, value);
		return value;
	}

	public long getTicks(String path, String defaultValue)
	{
		long value;
		String valueAsString = this._serverSpecificConfig.getString(path, defaultValue);
		value = TimeMisc.stringToTicks(valueAsString);
		this._plugin.dbgMinor( "Configuration \"%s\" = %d ticks" , path, value);
		return value;
	}

	public LocalTime getLocalTime(String path, LocalTime defaultValue) {
		String valueAsString = getStringInternal(path,defaultValue.toString());
		LocalTime value = LocalTime.parse(valueAsString);
		this._plugin.dbgMinor( "Configuration \"%s\" = %s" , path, value.toString());
		return value;
	}

	public List<Integer> getIntegerList(String path)
	{
		List<Integer> result = null;
		try {
			if (this._serverSpecificConfig.contains(path)) {
				result = this._serverSpecificConfig.getIntegerList(path);
			} else {
				result = this._commonConfig.getIntegerList(path);
			}
		} catch (Exception ex) {
			this._plugin.logWarn("Failed to read configuration \"%s\".", path);
			this._plugin.dbgMajor( "Exception: %s", ex.getMessage());
			result = new ArrayList<Integer>();
		}

		if (this._plugin.getEithonLogger().shouldDebug(DebugPrintLevel.MINOR)) {
			String s = "";
			boolean first = true;
			for (Integer integer : result) {
				if (first) first = false;
				else s += ", ";
				s += integer.toString();
			}
			this._plugin.dbgMinor( "Configuration \"%s\" = [%s]" , path, s);
		}
		return result;
	}

	public List<Long> getSecondsList(String path)
	{
		List<String> strings = null;
		try {
			if (this._serverSpecificConfig.contains(path)) {
				strings = this._serverSpecificConfig.getStringList(path);
			} else {
				strings = this._commonConfig.getStringList(path);
			}
		} catch (Exception ex) {
			this._plugin.logWarn("Failed to read configuration \"%s\".", path);
			this._plugin.dbgMajor( "Exception: %s", ex.getMessage());
			strings = new ArrayList<String>();
		}

		List<Long> result = new ArrayList<Long>();
		for (String string : strings) {
			long value = TimeMisc.stringToSeconds(string);
			result.add(value);
		}
		if (this._plugin.getEithonLogger().shouldDebug(DebugPrintLevel.MINOR)) {
			String s = "";
			boolean first = true;
			for (Long value : result) {
				if (first) first = false;
				else s += ", ";
				s += value.toString();
			}
			this._plugin.dbgMinor( "Configuration \"%s\" = [%s]" , path, s);
		}
		return result;
	}

	public List<String> getStringList(String path)
	{
		List<String> result = null;
		try {
			if (this._serverSpecificConfig.contains(path)) {
				result = this._serverSpecificConfig.getStringList(path);
			} else {
				result = this._commonConfig.getStringList(path);
			}
		} catch (Exception ex) {
			this._plugin.logWarn("Failed to read configuration \"%s\".", path);
			this._plugin.dbgMajor( "Exception: %s", ex.getMessage());
			result = new ArrayList<String>();
		}

		if (this._plugin.getEithonLogger().shouldDebug(DebugPrintLevel.MINOR)) {
			String s = "";
			boolean first = true;
			for (String string : result) {
				if (first) first = false;
				else s += ", ";
				s += string;
			}
			this._plugin.dbgMinor( "Configuration \"%s\" = [%s]" , path, s);
		}
		return result;
	}

	private File initializeConfigFile(JavaPlugin plugin, String fileName) {
		File file = new File(plugin.getDataFolder(), fileName);
		FileMisc.makeSureParentDirectoryExists(file);
		if(!file.exists()) {
			InputStream resource = plugin.getResource(fileName);
			if (resource != null) copy(resource, file);
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
			this._commonConfig.load(this._commonConfigFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this._serverSpecificConfig.load(this._serverSpecificConfigFile);
		} catch (Exception e) {
			this._plugin.logInfo("No server specific configuration file was loaded.");
		}
	}

	public void save()
	{
		try {
			this._serverSpecificConfig.save(this._serverSpecificConfigFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

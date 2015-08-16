package net.eithon.library.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("PermissionBasedMultiplier")
public class PermissionBasedMultiplier implements ConfigurationSerializable  {
	private HashMap<String, Multiplier> _multiplierMap;
	// Can be removed after testing
	private EithonPlugin _eithonPlugin;
	// Can be removed after testing
	private String _path;

	public static void initialize() {
		ConfigurationSerialization.registerClass(PermissionBasedMultiplier.class, "PermissionBasedMultiplier");
		ConfigurationSerialization.registerClass(Multiplier.class, "Multiplier");
	}

	private PermissionBasedMultiplier() {
		this._multiplierMap = new HashMap<String, Multiplier>();
	}

	private PermissionBasedMultiplier(Map<String, Object> map) {
		this._multiplierMap = toMultiplierMap(map);
	}

	public static PermissionBasedMultiplier loadFromConfiguration(EithonPlugin eithonPlugin, String path) {
		PermissionBasedMultiplier pbm;
		try {
			Map<String, Object> map = eithonPlugin.getConfiguration().getMap(path, true);
			if (map == null) {
				eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, 
						"getMap() for \"%s\" returned null", path);
				pbm = (PermissionBasedMultiplier) eithonPlugin.getConfiguration().getObject(path, null);
				if (pbm == null) {
					eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, 
							"getObject() for \"%s\" returned null", path);
				}
				eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, 
						"getObject() for \"%s\" returned %s", path, pbm.toString());
				return pbm;
			}
			pbm = deserialize(map);
			pbm._eithonPlugin = eithonPlugin;
			pbm._path = path;
			eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, 
					"getMap() for \"%s\" returned %s", path, pbm.toString());
			return pbm;
		} catch (Exception e)
		{
			e.printStackTrace();
			eithonPlugin.getEithonLogger().error("Could not load PermissionBasedMultiplier with path \"%s\".", path);
			pbm = new PermissionBasedMultiplier();
			pbm._eithonPlugin = eithonPlugin;
			pbm._path = path;
			return pbm;
		}
	}

	public double getMultiplier(Player player) {
		double result = 1.0;
		for (Multiplier multiplier : this._multiplierMap.values()) {
			if (!multiplier.hasPermission(player)) continue;
			double multiplierValue = multiplier.getValue();
			if (multiplierValue > result) result = multiplierValue;
		}
		return result;
	}

	public void test() {
		if (this._multiplierMap.size() > 0) return;
		this._multiplierMap.put("1", new Multiplier("donationboard.multipliers.1", 1.1));
		this._multiplierMap.put("2", new Multiplier("donationboard.multipliers.2", 1.2));
		this._multiplierMap.put("3", new Multiplier("donationboard.multipliers.3", 1.5));
		saveToConfiguration();
	}

	private void saveToConfiguration() {
		if (this._eithonPlugin == null) return;
		Configuration configuration = this._eithonPlugin.getConfiguration();
		configuration.setObject(this._path, this);
		configuration.save();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		this._multiplierMap.forEach((key, value) -> map.put(key, value));
		return map;
	}

	public static PermissionBasedMultiplier deserialize(Map<String, Object> map) {
		PermissionBasedMultiplier pbm = new PermissionBasedMultiplier();
		pbm._multiplierMap = toMultiplierMap(map);
		Bukkit.getLogger().info(String.format("PermissionBasedMultiplier.deserialize() = \"%s\"", pbm.toString()));
		return pbm;
	}

	@SuppressWarnings("unchecked")
	private static HashMap<String, Multiplier> toMultiplierMap(Map<String, Object> map) {
		HashMap<String, Multiplier> newMap = new HashMap<String, Multiplier>();		
		if (map == null) return newMap;
		for (Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (!(value instanceof Map<?, ?>)) {
				if (value instanceof String) {
					Bukkit.getLogger().warning(String.format("PermissionBasedMultiplier, map value is string: \"%s\"", value));
				} else if (value instanceof Multiplier) {
					Bukkit.getLogger().info(String.format("PermissionBasedMultiplier, map value is Multiplier: \"%s\"", value.toString()));
					newMap.put(entry.getKey(), (Multiplier) value);
				} else {
					Bukkit.getLogger().warning(String.format("PermissionBasedMultiplier, map value is not a map: \"%s\"", value.toString()));
				}
				continue;
			}
			newMap.put(entry.getKey(), Multiplier.deserialize((Map<String, Object>) value));
		}
		return newMap;
	}

	public String toString()
	{
		final StringBuilder result = new StringBuilder("");
		if (this._eithonPlugin != null) result.append(String.format("Plugin: %s, Path: %s", this._eithonPlugin.getName(), this._path));
		result.append("\nMultipliers:");
		if (this._multiplierMap != null) {
			this._multiplierMap.forEach((key, value) -> result.append(String.format("\n  %s = %s", key, value == null ? "null" : value.toString())));
		}
		return result.toString();
	}
}

package net.eithon.library.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("PermissionBasedMultiplier")
public class PermissionBasedMultiplier implements ConfigurationSerializable  {
	private HashMap<String, Multiplier> _multiplierMap;
	public static void initialize() {
		ConfigurationSerialization.registerClass(PermissionBasedMultiplier.class, "PermissionBasedMultiplier");
		ConfigurationSerialization.registerClass(Multiplier.class, "Multiplier");
	}

	public PermissionBasedMultiplier() {
		this._multiplierMap = new HashMap<String, Multiplier>();
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
			if (value instanceof Multiplier) {
				newMap.put(entry.getKey(), (Multiplier) value);
			} else if (value instanceof Map<?,?>) {
				newMap.put(entry.getKey(), Multiplier.deserialize((Map<String, Object>) value));
			}
		}
		return newMap;
	}

	public String toString()
	{
		final StringBuilder result = new StringBuilder("");
		if (this._multiplierMap == null) return result.toString();
		this._multiplierMap.forEach((key, value) -> result.append(String.format("\n  %s = %s", key, value == null ? "null" : value.toString())));
		return result.toString();
	}

	public static PermissionBasedMultiplier getFromConfig(Configuration config,
			String path) {
		PermissionBasedMultiplier defaultValue = new PermissionBasedMultiplier();
		Object object = config.getObject("multipliers.donationboard.mobKill", defaultValue);
		if (object instanceof PermissionBasedMultiplier) return (PermissionBasedMultiplier) object;
		Map<String, Object> map = config.getMap(path, true);
		return deserialize(map);
	}
}

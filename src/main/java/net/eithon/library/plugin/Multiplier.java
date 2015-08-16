package net.eithon.library.plugin;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("Multiplier")
public class Multiplier implements ConfigurationSerializable  {
	private String _permission;
	private double _value;
	
	private Multiplier() {}
	
	public Multiplier(String permission, double value) {
		this._permission = permission;
		this._value = value;
	}

	double getValue() { return this._value; }   
	
	boolean hasPermission(Player player) { return player.hasPermission(this._permission); }

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("permission", this._permission);
		map.put("value", this._value);
		return map;
	}

	public static Multiplier deserialize(Map<String, Object> map) {
		Multiplier multiplier = new Multiplier();
		multiplier._permission = (String) map.get("permission");
		Double value = (Double) map.get("value");
		multiplier._value = value == null? 1.0 : value.doubleValue();
		return multiplier;		
	}
	
	public String toString() {
		return String.format("%s: %.2f", this._permission, this._value);
	}
}

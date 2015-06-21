package net.eithon.library.json;

import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

public class Converter {
	
	@SuppressWarnings("unchecked")
	public static JSONObject fromVector(Vector vector)
	{
		JSONObject json = new JSONObject();
		json.put("x", vector.getX());
		json.put("y", vector.getY());
		json.put("z", vector.getZ());
		return json;
	}

	public static Vector toVector(JSONObject json)
	{
		double x = (double) json.get("x");
		double y = (double) json.get("y");
		double z = (double) json.get("z");
		return new Vector(x, y, z);
	}
}

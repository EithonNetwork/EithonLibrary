package net.eithon.library.extensions;

import net.eithon.library.json.JsonObject;
import net.eithon.library.plugin.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.json.simple.JSONObject;

public class EithonLocation extends JsonObject<EithonLocation>{

	private Location _location;
	private EithonWorld _eithonWorld;

	public EithonLocation(Location location) { this._location = location; }

	public EithonLocation() {
	}

	public Location getLocation() { return this._location; }

	public EithonWorld getEithonWorld() {
		if (this._location == null) return null;
		if (this._eithonWorld != null) return this._eithonWorld;
		this._eithonWorld = new EithonWorld(this._location.getWorld());
		return this._eithonWorld;
	}

	public Block searchForFirstBlockOfMaterial(Material material, int maxDistance) {
		int x1 = this._location.getBlockX(); 
		int y1 = this._location.getBlockY();
		int z1 = this._location.getBlockZ();

		World world = this._location.getWorld();

		for (int distance = 0; distance < maxDistance; distance++) {
			for (int xPoint = x1-distance; xPoint <= x1+distance; xPoint++) {
				int currentDistance = Math.abs(xPoint-x1);
				boolean okDistanceX = (currentDistance == distance);
				for (int yPoint = y1-distance; yPoint <= y1+distance; yPoint++) {
					currentDistance = Math.abs(yPoint-y1);
					boolean okDistanceY = (currentDistance == distance);
					for (int zPoint = z1-distance; zPoint <= z1+distance; zPoint++) {
						currentDistance = Math.abs(zPoint-z1);
						boolean okDistanceZ = (currentDistance == distance);
						if (!okDistanceX && !okDistanceY && !okDistanceZ) continue;
						Block currentBlock = world.getBlockAt(xPoint, yPoint, zPoint);
						if (currentBlock.getType() == material) return currentBlock;
					}
				}
			}
		}

		return null;
	}

	@Override
	public EithonLocation factory() {
		return new EithonLocation();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object toJson() {
		JSONObject json = new JSONObject();
		json.put("world", getEithonWorld().toJson());
		json.put("x", this._location.getX());
		json.put("y", this._location.getY());
		json.put("z", this._location.getZ());
		json.put("yaw", this._location.getYaw());
		json.put("pitch", this._location.getPitch());
		return json;
	}

	@Override
	public EithonLocation fromJson(Object json) {
		JSONObject jsonObject = (JSONObject) json;
		this._eithonWorld = EithonWorld.getFromJson(jsonObject.get("world"));
		double x = (double) jsonObject.get("x");
		double y = (double) jsonObject.get("y");
		double z = (double) jsonObject.get("z");
		float yaw = (float) (double) jsonObject.get("yaw");
		float pitch = (float) (double) jsonObject.get("pitch");
		World world = this._eithonWorld.getWorld();
		if (world == null) {
			Logger.libraryWarning("EithonLocation.fromJson: Could not find world %s", this._eithonWorld.getName());
			return null;
		}
		this._location = new Location(world, x, y, z, yaw, pitch);
		return this;
	}
	
	public static EithonLocation getFromJson(Object json) {
		return new EithonLocation().fromJson(json);
	}

}

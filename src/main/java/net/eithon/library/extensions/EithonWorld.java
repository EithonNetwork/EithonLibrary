package net.eithon.library.extensions;

import java.util.UUID;

import net.eithon.library.core.IUuidAndName;
import net.eithon.library.json.IJson;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.json.simple.JSONObject;

public class EithonWorld implements IJson<EithonWorld>, IUuidAndName{

	private World _world = null;
	private UUID _id = null;

	public EithonWorld(World world) { 
		this._world = world; 
		if (world != null) {
			this._id = world.getUID();
		}
	}

	public EithonWorld() {
	}

	@Override
	public UUID getUniqueId() { return this._id; }

	@Override
	public String getName() { return this._world.getName(); }

	public World getWorld() { 
		if (this._world != null) return this._world;
		return Bukkit.getWorld(this._id);
	}	

	public static EithonWorld getFromString(String worldIdOrName) {
		World world = null;
		try {
			UUID id = UUID.fromString(worldIdOrName);
			world = Bukkit.getWorld(id);
		} catch (Exception e) { }
		if (world == null) try { world = Bukkit.getWorld(worldIdOrName); } catch (Exception e) { }
		if (world == null) return null;
		return new EithonWorld(world);
	}

	@Override
	public EithonWorld factory() {
		return new EithonWorld();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object toJson() {
		JSONObject json = new JSONObject();
		json.put("id", getUniqueId().toString());
		json.put("name", getName());
		return json;
	}

	@Override
	public EithonWorld fromJson(Object json) {
		JSONObject jsonObject = (JSONObject) json;
		this._id = UUID.fromString((String) jsonObject.get("id"));
		return this;
	}
	
	public static EithonWorld getFromJson(Object json) {
		return new EithonWorld().fromJson(json);
	}
}

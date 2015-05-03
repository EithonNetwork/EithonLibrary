package net.eithon.library.extensions;

import net.eithon.library.json.IJson;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.json.simple.JSONObject;

public class EithonBlock implements IJson<EithonBlock>{

	private Block _block;

	public EithonBlock(Block block) { this._block = block; }

	EithonBlock() {
	}

	public Block getBlock() { return this._block; }
	
	public EithonLocation getEithonLocation() {
		Location location = new Location(
				this._block.getWorld(),
				this._block.getX() + 0.5,
				this._block.getY() + 0.5,
				this._block.getZ() + 0.5);
		return new EithonLocation(location);
	}

	@Override
	public EithonBlock factory() {
		return new EithonBlock();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object toJson() {
		JSONObject json = new JSONObject();
		EithonWorld world = new EithonWorld(this._block.getWorld());
		json.put("world", world.toJson());
		json.put("x", this._block.getX());
		json.put("y", this._block.getY());
		json.put("z", this._block.getZ());
		return json;
	}

	@Override
	public EithonBlock fromJson(Object json) {
		JSONObject jsonObject = (JSONObject) json;
		World world = EithonWorld.getFromJson(jsonObject.get("world")).getWorld();
		int x = (int) jsonObject.get("x");
		int y = (int) jsonObject.get("y");
		int z = (int) jsonObject.get("z");
		this._block = world.getBlockAt(x, y, z);
		return this;
	}
	
	public static EithonBlock getFromJson(Object json) {
		return new EithonBlock().fromJson(json);
	}
}

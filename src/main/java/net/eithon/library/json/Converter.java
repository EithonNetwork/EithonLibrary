package net.eithon.library.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.UUID;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.Logger;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Converter {
	public static long toLong(Object o, long defaultValue)
	{
		if (o == null) return defaultValue;
		return (long) o;
	}
	
	@SuppressWarnings("unchecked")	
	@Deprecated // Use EithonLocation instead
	public static JSONObject fromLocation(Location location, boolean withWorld)
	{
		JSONObject json = new JSONObject();
		if (withWorld) json.put("world", fromWorld(location.getWorld()));
		json.put("x", location.getX());
		json.put("y", location.getY());
		json.put("z", location.getZ());
		json.put("yaw", location.getYaw());
		json.put("pitch", location.getPitch());
		return json;
	}

	@Deprecated // Use EithonLocation instead
	public static Location toLocation(JSONObject json, World world)
	{
		if (world == null) {
			world = toWorld((JSONObject) json.get("world"));
		}
		double x = (double) json.get("x");
		double y = (double) json.get("y");
		double z = (double) json.get("z");
		float yaw = (float) (double) json.get("yaw");
		float pitch = (float) (double) json.get("pitch");
		return new Location(world, x, y, z, yaw, pitch);
	}
	
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

	@SuppressWarnings("unchecked")
	@Deprecated // Use EithonBlock instead
	public static JSONObject fromBlock(Block block, boolean withWorld)
	{
		JSONObject json = new JSONObject();
		if (withWorld) json.put("world", fromWorld(block.getWorld()));
		json.put("x", block.getX());
		json.put("y", block.getY());
		json.put("z", block.getZ());
		return json;
	}

	@Deprecated // Use EithonBlock instead
	public static Block toBlock(JSONObject json, World world)
	{
		if (world == null) {
			world = toWorld((JSONObject) json.get("world"));
		}
		int x = (int) json.get("x");
		int y = (int) json.get("y");
		int z = (int) json.get("z");
		return world.getBlockAt(x, y, z);
	}

	@SuppressWarnings("unchecked")
	@Deprecated // Use EithonPlayer instead
	public static JSONObject fromPlayer(Player player)
	{
		JSONObject json = new JSONObject();
		json.put("id", player.getUniqueId().toString());
		json.put("name", player.getName());
		return json;
	}

	@SuppressWarnings("unchecked")
	@Deprecated // Use EithonPlayer instead
	public static JSONObject fromPlayer(UUID id, String name)
	{
		JSONObject json = new JSONObject();
		json.put("id", id.toString());
		json.put("name", name);
		return json;
	}

	@Deprecated // Use EithonPlayer instead
	public static Player toPlayer(JSONObject json)
	{
		UUID id = UUID.fromString((String) json.get("id"));
		Player player = Bukkit.getPlayer(id);
		if (player == null) {
			String name = (String) json.get("name");
			player = Bukkit.getPlayer(name);				
		}
		return player;
	}

	@Deprecated // Use EithonPlayer instead
	public static UUID toPlayerId(JSONObject json)
	{
		return UUID.fromString((String) json.get("id"));
	}

	@Deprecated // Use EithonPlayer instead
	public static String toPlayerName(JSONObject json)
	{
		return (String) json.get("name");
	}

	@SuppressWarnings("unchecked")
	@Deprecated // Use EithonWorld instead
	public static JSONObject fromWorld(World world)
	{
		JSONObject json = new JSONObject();
		json.put("id", world.getUID().toString());
		json.put("name", world.getName());
		return json;
	}

	@Deprecated // Use EithonWorld instead
	public static World toWorld(JSONObject json)
	{
		UUID id = UUID.fromString((String) json.get("id"));
		World world = Bukkit.getWorld(id);
		if (world == null) {
			String name = (String) json.get("name");
			world = Bukkit.getWorld(name);				
		}
		return world;
	}

	@Deprecated // Use EithonWorld instead
	public static UUID toWorldId(JSONObject json)
	{
		return (UUID) UUID.fromString((String) json.get("id"));
	}

	@Deprecated // Use EithonWorld instead
	public static String toWorldName(JSONObject json)
	{
		return (String) json.get("name");
	}

	@SuppressWarnings("unchecked")
	@Deprecated // Use json.FileContent instead
	public static JSONObject fromBody(String name, int version, Object payload)
	{
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("version", version);
		json.put("payload", payload);
		return json;
	}

	@Deprecated // Use json.FileContent instead
	public static Object toBodyPayload(JSONObject json)
	{
		return json.get("payload");
	}

	@Deprecated // Use json.FileContent instead
	public static String toBodyName(JSONObject json)
	{
		return (String) json.get("name");
	}

	@Deprecated // Use json.FileContent instead
	public static int toBodyVersion(JSONObject json)
	{
		return (int) json.get("version");
	}

	@Deprecated // Use json.FileContent instead
	public static void save(File file, JSONObject data) {
		FileMisc.makeSureParentDirectoryExists(file);
		try {
			Writer writer = new FileWriter(file);
			data.writeJSONString(writer);
			writer.close();
			Logger.libraryDebug(DebugPrintLevel.MAJOR, "Saved \"%s\".", file.getName());
		} catch (IOException e) {
			Logger.libraryWarning("Can't create file \"%s\" for save: %s", file.getName(), e.getMessage());
		} catch (Exception e) {
			Logger.libraryError("Failed to save file \"%s\": %s", file.getName(), e.getMessage());
		}
	}

	@Deprecated // Use json.FileContent instead
	public static JSONObject load(EithonPlugin plugin, File file) {
		FileMisc.makeSureParentDirectoryExists(file);
		JSONObject data = null;
		Reader reader = null;
		try {
			reader = new FileReader(file);
			Object o = JSONValue.parseWithException(reader);
			if (o == null) {
				Logger.libraryDebug(DebugPrintLevel.MINOR, "Load; parse returned null.");
				return null;
			}
			if (!(o instanceof JSONObject)) {
				Logger.libraryError("Could not cast content of file \"%s\" to a JSONObject.");
				return null;
			}
			data = (JSONObject) o;
			reader.close();
			Logger.libraryDebug(DebugPrintLevel.MAJOR, "Loaded \"%s\".", file.getName());
		} catch (FileNotFoundException e) {
			Logger.libraryWarning("Can't open file \"%s\" for load: %s", file.getName(), e.getMessage());
		} catch (Exception e) {
			Logger.libraryError("Failed to load file \"%s\": %s", file.getName(), e.getMessage());
		} finally {
			if (reader != null) try { reader.close(); } catch (IOException e) {}
		}
		return data;
	}

	@Deprecated // Use json.FileContent instead
	public static void delayedSave(File file, JSONObject data, JavaPlugin plugin) {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				save(file, data);
			}
		});		
	}
}

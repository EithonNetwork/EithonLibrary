package net.eithon.library.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.Logger;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class FileContent implements IJson<FileContent>{
	private String _name;
	private long _version;
	private Object _payload;
	
	FileContent() {
	}
	
	public FileContent(String name, int version, Object payload) {
		this._name = name;
		this._version = version;
		this._payload = payload;
	}
	
	public String getName() { return this._name; }
	public long getVersion() { return this._version; }
	public Object getPayload() { return this._payload; }

	@Override
	public FileContent factory() {
		return new FileContent();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object toJson() {
		JSONObject json = new JSONObject();
		json.put("name", this._name);
		json.put("version", this._version);
		json.put("payload", this._payload);
		return json;
	}

	@Override
	public FileContent fromJson(Object json) {
		JSONObject jsonObject = (JSONObject) json;
		this._name = (String) jsonObject.get("name");
		this._version = (long) jsonObject.get("version");
		this._payload = jsonObject.get("payload");
		return this;
	}

	public void save(File file) {
		FileMisc.makeSureParentDirectoryExists(file);
		if (file.exists()) {
			Logger.libraryError("Did not expect file \"%s\" to exist.", file.getAbsolutePath());
		}
		JSONObject data = (JSONObject) this.toJson();
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

	public static FileContent loadFromFile(File file) {
		FileContent fileContent = new FileContent();
		if (!fileContent.load(file)) return null;
		return fileContent;
	}

	public boolean load(File file) {
		FileMisc.makeSureParentDirectoryExists(file);
		JSONObject data = null;
		Reader reader = null;
		try {
			reader = new FileReader(file);
			Object o = JSONValue.parseWithException(reader);
			if (o == null) {
				Logger.libraryDebug(DebugPrintLevel.MINOR, "Load; parse returned null.");
				return false;
			}
			if (!(o instanceof JSONObject)) {
				Logger.libraryError("Could not cast content of file \"%s\" to a JSONObject.");
				return false;
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
		if (data == null) return false;
		fromJson(data);
		return true;
	}

	public void delayedSave(File file, JavaPlugin plugin) {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				save(file);
			}
		});		
	}

}

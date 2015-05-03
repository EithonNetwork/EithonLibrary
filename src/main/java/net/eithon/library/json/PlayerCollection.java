package net.eithon.library.json;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import net.eithon.library.core.IFactory;
import net.eithon.library.core.IUuidAndName;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.file.FileMisc;
import net.eithon.library.plugin.Logger;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.json.simple.JSONArray;

public class PlayerCollection<T extends IJson<T> & IUuidAndName>
extends net.eithon.library.core.PlayerCollection<T> 
implements Iterable<T>, IJsonDelta<PlayerCollection<T>>, Serializable
{
	private static final long serialVersionUID = 1L;
	private T _infoInstance;
	private File _deltaFolder;
	private int _nextDelta;

	public PlayerCollection(T instance, File deltaFolder) {
		this.playerInfo = new HashMap<UUID, T>();
		this._infoInstance = instance;
		this._deltaFolder = deltaFolder;
		this._nextDelta = 0;
	}

	public PlayerCollection(IFactory<T> factory) {
		this(factory.factory(), null);
	}

	public PlayerCollection(T instance) {
		this(instance, null);
	}

	@Override
	public Object toJson() {
		return toJsonDelta(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object toJsonDelta(boolean saveAll) {
		JSONArray json = new JSONArray();
		for (T value : this.playerInfo.values()) {
			if (!(value instanceof IJson<?>)) {
				Logger.libraryError("%s must implement interface IJson", value.toString());
				return null;
			}
			Object infoAsJson = null;
			if (saveAll) {
				IJson<T> info = (IJson<T>) value;
				infoAsJson = info.toJson();
			} else {
				if (!(value instanceof IJsonDelta<?>)) {
					Logger.libraryError("%s must implement interface IJsonDelta", value.toString());
					return null;
				}
				IJsonDelta<T> info = (IJsonDelta<T>) value;
				infoAsJson = info.toJsonDelta(false);			
			}
			if (infoAsJson != null) {
				json.add(infoAsJson);
			} else {
				json.add(infoAsJson);
			}
		}
		return json;
	}

	@Override
	public PlayerCollection<T> fromJson(Object json) {
		JSONArray jsonArray = (JSONArray) json;
		this.playerInfo = new HashMap<UUID, T>();
		if ((jsonArray == null) || (jsonArray.size() == 0)) return this;
		for (Object o : jsonArray) {
			if (o == null) continue;
			T info = this._infoInstance.factory();
			info.fromJson(o);
			this.playerInfo.put(info.getUniqueId(), info);
		}
		return this;
	}

	@Override
	public PlayerCollection<T> factory() {
		return new PlayerCollection<T>(this._infoInstance);
	}

	public void saveDelta(EithonPlugin eithonPlugin, String name, int version)
	{
		saveDelta(eithonPlugin, name, version, false, null);
	}

	public void saveDelta(EithonPlugin eithonPlugin, String name, int version, boolean saveAll)
	{
		saveDelta(eithonPlugin, name, version, saveAll, null);
	}

	public void saveDelta(EithonPlugin eithonPlugin, String name, int version, boolean saveAll, File archiveFile)
	{
		File file = getFile(this._nextDelta);
		JSONArray jsonDelta = (JSONArray) toJsonDelta(saveAll);
		if ((jsonDelta.size() == 0) || (jsonDelta.get(0) == null)) {
			eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE,
					"File \"%s\" is not saved, due to no data to save for %s.", 
					file.getName(), name);
			return;
		}
		FileContent fileContent = new FileContent(name, version, jsonDelta);
		if (eithonPlugin.isEnabled()) fileContent.delayedSave(file, eithonPlugin, archiveFile);
		else fileContent.save(file);
		this._nextDelta++;
	}

	public File getFile(int index) {
		return new File(this._deltaFolder, getFileName(index));
	}

	public String getFileName(int index) {
		return String.format("delta_%06d.json", index);
	}

	public void consolidateDelta(EithonPlugin eithonPlugin, String name, int version, File archiveFile)
	{
		this.playerInfo = new HashMap<UUID, T>();
		if (this._deltaFolder == null) return;
		File[] files = FileMisc.getFilesOrderByLastModified(this._deltaFolder, ".json", false);
		if (files != null) {
			eithonPlugin.getEithonLogger().debug(DebugPrintLevel.MAJOR, "Loading %d files", files.length);
			for (File file : files) {
				eithonPlugin.getEithonLogger().debug(DebugPrintLevel.MINOR, "Loading file \"%s\".", file.getName());
				PlayerCollection<T> delta = loadDeltaFile(file);
				aggregateDelta(delta);
				file.delete();
			}
		}
		this._nextDelta = 0;
		saveDelta(eithonPlugin, name, version, true, archiveFile);
	}

	private void aggregateDelta(PlayerCollection<T> playerTimes) {
		for (T playerTime : playerTimes) {
			if (this.playerInfo.containsKey(playerTime.getUniqueId())) continue;
			this.playerInfo.put(playerTime.getUniqueId(), playerTime);
		}
	}

	private PlayerCollection<T> loadDeltaFile(File file) {
		FileContent fileContent = FileContent.loadFromFile(file);
		return new PlayerCollection<T>(this._infoInstance).fromJson(fileContent.getPayload());
	}

	public int size() {	return this.playerInfo.size(); }

	public Object[] toArray() { return this.playerInfo.values().toArray(); }

	public Collection<T> values() { return this.playerInfo.values(); }

	@Deprecated
	public T[] toArray(T[] playerTimes) { return this.playerInfo.values().toArray(playerTimes); }
}
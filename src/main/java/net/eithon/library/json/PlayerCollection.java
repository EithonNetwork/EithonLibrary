package net.eithon.library.json;

import java.io.File;
import java.io.Serializable;
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
implements Iterable<T>, IJson<PlayerCollection<T>>, Serializable
{
	private static final long serialVersionUID = 1L;
	private HashMap<UUID, T> _playerInfo = null;
	private T _infoInstance;
	private File _deltaFolder;
	private int _nextDelta;

	public PlayerCollection(T instance, File deltaFolder) {
		this._playerInfo = new HashMap<UUID, T>();
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

	@SuppressWarnings("unchecked")
	public Object toJson() {
		JSONArray json = new JSONArray();
		for (T value : this._playerInfo.values()) {
			if (!(value instanceof IJson<?>)) {
				Logger.libraryError("%s must implement interface J", value.toString());
				return null;
			}
			IJson<T> info = (IJson<T>) value;
			Object infoAsJson = info.toJson();
			if (infoAsJson != null) json.add(infoAsJson);
		}
		return json;
	}

	@Override
	public PlayerCollection<T> fromJson(Object json) {
		JSONArray jsonArray = (JSONArray) json;
		HashMap<UUID, T> playerInfo = new HashMap<UUID, T>();
		for (Object o : jsonArray) {
			T info = this._infoInstance.factory();
			info.fromJson(o);
			playerInfo.put(info.getUniqueId(), info);
		}
		this._playerInfo = playerInfo;
		return this;
	}

	@Override
	public PlayerCollection<T> factory() {
		return new PlayerCollection<T>(this._infoInstance);
	}

	public void saveDelta(EithonPlugin eithonPlugin)
	{
		String fileName = String.format("delta_%06d.json", this._nextDelta++);
		File file = new File(this._deltaFolder, fileName);
		FileContent fileContent = new FileContent("PlayerTimes", 1, toJson());
		fileContent.delayedSave(file, eithonPlugin);
	}

	public void consolidateDelta(EithonPlugin eithonPlugin)
	{
		this._playerInfo = new HashMap<UUID, T>();
		if (this._deltaFolder == null) return;
		File[] files = FileMisc.getFilesOrderByLastModified(this._deltaFolder, ".json", false);
		eithonPlugin.getEithonLogger().debug(DebugPrintLevel.MAJOR, "Loading %d files", files.length);
		if (files != null) {
			for (File file : files) {
				eithonPlugin.getEithonLogger().debug(DebugPrintLevel.MINOR, "Loading file \"%s\".", file.getName());
				PlayerCollection<T> delta = loadDeltaFile(file);
				aggregateDelta(delta);
				file.delete();
			}
		}
		this._nextDelta = 0;
		saveDelta(eithonPlugin);
	}

	private void aggregateDelta(PlayerCollection<T> playerTimes) {
		for (T playerTime : playerTimes) {
			if (this._playerInfo.containsKey(playerTime.getUniqueId())) continue;
			this._playerInfo.put(playerTime.getUniqueId(), playerTime);
		}
	}

	private PlayerCollection<T> loadDeltaFile(File file) {
		FileContent fileContent = FileContent.loadFromFile(file);
		return new PlayerCollection<T>(this._infoInstance).fromJson(fileContent.getPayload());
	}
}
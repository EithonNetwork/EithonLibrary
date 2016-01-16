package net.eithon.library.time;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class TemporaryEffect {
	private Plugin _plugin;
	private ITemporaryEffect _doUndo;
	
	public TemporaryEffect(Plugin plugin, ITemporaryEffect doUndo) {
		this._plugin = plugin;
		this._doUndo = doUndo;
	}
	
	public void run(long ticksBeforeUndo, Object... args) {
		final ITemporaryEffect doUndo = this._doUndo;
		Object context = doUndo.Do(args);
		Bukkit.getScheduler().scheduleSyncDelayedTask(this._plugin, new Runnable() {
			public void run() {
				doUndo.Undo(context, args);
			}
		}, ticksBeforeUndo);
	}
}

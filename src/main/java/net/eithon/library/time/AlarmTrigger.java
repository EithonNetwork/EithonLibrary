package net.eithon.library.time;

import java.time.LocalDateTime;
import java.util.ArrayList;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class AlarmTrigger {
	private static final long TICK_LENGTH = 100L;
	private static AlarmTrigger singleton = null;
	private EithonPlugin _plugin = null;
	private ArrayList<Alarm> _alarms = new ArrayList<Alarm>();
	private int _enableCounter = 0;
	private int _firstAlarmIndex= -1;

	private AlarmTrigger() {
	}

	public static AlarmTrigger get()
	{
		if (singleton == null) {
			singleton = new AlarmTrigger();
		}
		return singleton;
	}

	public void enable(EithonPlugin plugin){
		this._plugin = plugin;
		this._enableCounter++;
		this._firstAlarmIndex = getFirstAlarmIndex();
		tick(this._enableCounter);
	}

	public void disable() {
		this._plugin = null;
	}

	public void setAlarm(String name, LocalDateTime time, Runnable task)
	{
		this.isEnabledOrWarn();
		
		synchronized(this) {
			Alarm alarm = new Alarm(name, time, task);
			addToAlarmQueue(alarm);
		}
	}

	public void repeat(String name, long secondsBetweenRepeat, IRepeatable task)
	{
		this.isEnabledOrWarn();
		synchronized(this) {
			LocalDateTime time = LocalDateTime.now().plusSeconds(secondsBetweenRepeat);
			Alarm alarm = new Alarm(name, time, new Runnable() {
				@Override
				public void run() {
					boolean repeat = task.repeat();
					if (repeat) repeat(name, secondsBetweenRepeat, task);
				}
			});
			addToAlarmQueue(alarm);
		}
	}

	private void addToAlarmQueue(Alarm alarm) {
		this._alarms.add(alarm);
		Alarm firstAlarm = getFirstAlarm();
		if ((firstAlarm == null) || firstAlarm.getTime().isAfter(alarm.getTime())) {
			this._firstAlarmIndex = this._alarms.size()-1;	
		}
	}

	private boolean isEnabled()
	{
		return (this._plugin != null);
	}

	private boolean isEnabledOrWarn()
	{
		if (this._plugin != null) return true;
		Logger.libraryWarning("The AlarmTrigger has not been enabled");
		return false;
	}

	void tick(int enableCounter) {
		final int currentCounter = enableCounter;
		if (!isEnabledOrWarn()) return;
		if (enableCounter < this._enableCounter) return;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(this._plugin, new Runnable() {
			public void run() {
				tick(currentCounter);
			}
		}, TICK_LENGTH);
		scheduler.scheduleSyncDelayedTask(this._plugin, new Runnable() {
			public void run() {
				checkAlarms();
			}
		});
	}

	void checkAlarms() 
	{
		synchronized(this)
		{
			Alarm firstAlarm = getFirstAlarm();
			while ((firstAlarm != null) && firstAlarm.maybeSetOff()) {
				Logger.libraryDebug(DebugPrintLevel.VERBOSE, "Alarm was set off: %s", firstAlarm.toString());
				this._alarms.remove(this._firstAlarmIndex);
				this._firstAlarmIndex = getFirstAlarmIndex();
				firstAlarm = this._alarms.get(this._firstAlarmIndex);				
			}
		}
	}

	private int getFirstAlarmIndex()
	{
		if (this._alarms.size() < 1) return -1;
		Alarm alarm = this._alarms.get(0);
		LocalDateTime firstAlarmTime = alarm.getTime();
		int firstAlarmIndex = 0;
		for (int i = 1; i < this._alarms.size(); i++) {
			alarm = this._alarms.get(i);
			if (alarm.getTime().isBefore(firstAlarmTime)) {
				firstAlarmIndex = i;
				firstAlarmTime = alarm.getTime();
			}		
		}

		return firstAlarmIndex;
	}

	private Alarm getFirstAlarm()
	{
		if (hasNoAlarms()) return null;
		return this._alarms.get(this._firstAlarmIndex);
	}

	private boolean hasNoAlarms() {
		return this._firstAlarmIndex == -1;
	}
}

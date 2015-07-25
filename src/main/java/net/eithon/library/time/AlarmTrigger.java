package net.eithon.library.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.UUID;

import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger;
import net.eithon.library.plugin.Logger.DebugPrintLevel;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class AlarmTrigger {
	private static final long TICK_LENGTH = 100L;
	private static AlarmTrigger singleton = null;
	private EithonPlugin _plugin = null;
	private HashMap<UUID, Alarm> _alarms = new HashMap<UUID, Alarm>();
	private int _enableCounter = 0;
	private UUID _firstAlarmId= null;

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
		this._firstAlarmId = getFirstAlarmId();
		tick(this._enableCounter);
	}

	public void disable() {
		this._plugin = null;
	}

	public UUID setAlarm(String name, long secondsToAlarm, Runnable task)
	{
		LocalDateTime time = LocalDateTime.now().plusSeconds(secondsToAlarm);
		return setAlarm(name, time, task);
	}

	public UUID setAlarm(String name, LocalDateTime when, Runnable task)
	{
		this.isEnabledOrWarn();

		Alarm alarm; 
		synchronized(this) {
			alarm = new Alarm(name, when, task);
			addToAlarmQueue(alarm);
		}
		return alarm.getId();
	}

	public boolean removeAlarm(UUID id)
	{
		synchronized (this) {
			Alarm alarm = this._alarms.get(id);
			if (alarm == null) return false;
			this._alarms.remove(id);
			return true;
		}
	}

	public boolean resetAlarm(UUID id, long secondsToAlarm)
	{
		LocalDateTime time = LocalDateTime.now().plusSeconds(secondsToAlarm);
		return resetAlarm(id, time);
	}

	public boolean resetAlarm(UUID id, LocalDateTime when)
	{
		this.isEnabledOrWarn();
		if (id == null) return false;

		Alarm alarm; 
		synchronized(this) {
			alarm = this._alarms.get(id);
			if (alarm == null) return false;
			alarm.reset(when);
			this._firstAlarmId = getFirstAlarmId();
		}
		return true;
	}

	public void repeat(String name, long secondsBetweenRepeat, IRepeatable task)
	{
		this.isEnabledOrWarn();
		synchronized(this) {
			LocalDateTime time = LocalDateTime.now().plusSeconds(secondsBetweenRepeat);
			Alarm alarm = new Alarm(name, time, new Runnable() {
				public void run() {
					boolean repeat = task.repeat();
					if (repeat) repeat(name, secondsBetweenRepeat, task);
				}
			});
			addToAlarmQueue(alarm);
		}
	}

	public void repeatEveryDay(String name, LocalTime timeOfDay, IRepeatable task) {
		LocalDateTime time = null;
		LocalDateTime alarmToday = LocalDateTime.of(LocalDate.now(), timeOfDay);
		LocalDateTime alarmTomorrow = LocalDateTime.of(LocalDate.now().plusDays(1),timeOfDay);
		time = (LocalDateTime.now().isBefore(alarmToday)) ? alarmToday : alarmTomorrow;
		Alarm alarm = new Alarm(name, time, new Runnable() {
			public void run() {
				boolean repeat = task.repeat();
				if (repeat) repeatEveryDay(name, timeOfDay, task);
			}
		});
		addToAlarmQueue(alarm);
	}

	private void addToAlarmQueue(Alarm alarm) {
		this._alarms.put(alarm.getId(), alarm);
		this._firstAlarmId = getFirstAlarmId();
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
				if (firstAlarm.hasBeenSetOff()) this._alarms.remove(this._firstAlarmId);
				this._firstAlarmId = getFirstAlarmId();
				firstAlarm = getFirstAlarm();			
			}
		}
	}

	private UUID getFirstAlarmId()
	{
		UUID firstId = null;
		LocalDateTime firstAlarmTime = LocalDateTime.MAX;
		for (Alarm alarm : this._alarms.values()) {
			if (alarm.getTime().isBefore(firstAlarmTime)) {
				firstId = alarm.getId();
				firstAlarmTime = alarm.getTime();
			}			
		}

		return firstId;
	}

	private Alarm getFirstAlarm()
	{
		if (hasNoAlarms()) return null;
		return this._alarms.get(this._firstAlarmId);
	}

	private boolean hasNoAlarms() {
		return this._firstAlarmId == null;
	}
}

package net.eithon.library.time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.UUID;

import javax.persistence.TemporalType;

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

	public void repeatEveryHour(String name, int minute, IRepeatable task) {
		LocalDateTime nextAlarmTime = getNextHour(minute);
		Alarm alarm = new Alarm(name, nextAlarmTime, new Runnable() {
			public void run() {
				boolean repeat = task.repeat();
				if (repeat) repeatEveryHour(name, minute, task);
			}
		});
		addToAlarmQueue(alarm);
	}

	private LocalDateTime getNextHour(int minute) {
		LocalDateTime time = LocalDateTime.now();
		int currentMinute = time.getMinute();
		if (currentMinute >= minute) time.plusHours(1);
		time.truncatedTo(ChronoUnit.HOURS);
		time.plusMinutes(minute);
		return time;
	}

	public void repeatEveryDay(String name, LocalTime timeOfDay, IRepeatable task) {
		LocalDateTime time = getNextTimeOnDayBasis(timeOfDay, 1);
		Alarm alarm = new Alarm(name, time, new Runnable() {
			public void run() {
				boolean repeat = task.repeat();
				if (repeat) repeatEveryDay(name, timeOfDay, task);
			}
		});
		addToAlarmQueue(alarm);
	}
	
	public void repeatEveryWeek(String name, DayOfWeek dayOfWeek, LocalTime timeOfDay, IRepeatable task) {
		LocalDateTime time = null;
		LocalDateTime now = LocalDateTime.now();
		DayOfWeek nowDayOfWeek = now.getDayOfWeek();
		if (nowDayOfWeek == dayOfWeek) {
			time = getNextTimeOnDayBasis(timeOfDay, 7);
		} else {
			int day1 = nowDayOfWeek.get(ChronoField.DAY_OF_WEEK);
			int day2 = dayOfWeek.get(ChronoField.DAY_OF_WEEK);
			int daysToNext = day2-day1;
			if (daysToNext < 0) daysToNext += 7;
			time = LocalDateTime.of(LocalDate.now().plusDays(daysToNext),timeOfDay);
		}
		Alarm alarm = new Alarm(name, time, new Runnable() {
			public void run() {
				boolean repeat = task.repeat();
				if (repeat) repeatEveryWeek(name, dayOfWeek, timeOfDay, task);
			}
		});
		addToAlarmQueue(alarm);
	}

	LocalDateTime getNextTimeOnDayBasis(LocalTime timeOfDay, int daysLater) {
		LocalDateTime alarmToday = LocalDateTime.of(LocalDate.now(), timeOfDay);
		if (LocalDateTime.now().isBefore(alarmToday)) return alarmToday;
		return LocalDateTime.of(LocalDate.now().plusDays(daysLater),timeOfDay);
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

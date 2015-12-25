package net.eithon.library.time;

import java.time.LocalDateTime;

public class TimeMisc {
	public static LocalDateTime toLocalDateTime(Object o) {
		if (o == null) return null;
		return LocalDateTime.parse((String) o);
	}
	
	public static String fromLocalDateTime(LocalDateTime time) {
		if (time == null) return null;
		return time.toString();
	}
	
	public static long secondsToTicks(double seconds)
	{
		return Math.round(20*seconds);
	}

	public static double ticksToSeconds(long ticks)
	{
		return ticks/20.0;
	}
	
	public static long stringToSeconds(String time) {
		if (time == null) return 0;
		if (time.endsWith("t")) {
			try {
				time = time.substring(0, time.length()-1);
				return (long) TimeMisc.ticksToSeconds(Long.parseLong(time));
			} catch (Exception e) {}
		} else 	if (time.endsWith("s")) {
			try {
				time = time.substring(0, time.length()-1);
				return Long.parseLong(time);
			} catch (Exception e) {}
		} else if (time.endsWith("m")) {
			try {
				time = time.substring(0, time.length()-1);
				return Long.parseLong(time)*60;
			} catch (Exception e) {}			
		} else if (time.endsWith("h")) {
			try {
				time = time.substring(0, time.length()-1);
				return Long.parseLong(time)*3600;
			} catch (Exception e) {}			
		}
		String[] parts = time.split(":");
		switch (parts.length) {
		case 1:
			return Long.parseLong(parts[0]);
		case 2:
			return Long.parseLong(parts[0]) * 60 + Long.parseLong(parts[1]);
		case 3:
			return Long.parseLong(parts[0]) * 3600 + Long.parseLong(parts[1]) * 60 + Long.parseLong(parts[2]);
		default:
			break;
		}
		return 0;
	}
	
	public static long stringToTicks(String time) {
		if (time == null) return 0;
		if (time.endsWith("t")) {
			try {
				time = time.substring(0, time.length()-1);
				return Long.parseLong(time);
			} catch (Exception e) {}
		} else return TimeMisc.secondsToTicks(stringToSeconds(time));
		return 0;
	}
	
	public static String secondsToString(long seconds) {
		return secondsToString(seconds, false);
	}
	
	public static String secondsToString(long seconds, boolean showDays) {
		if (seconds < 60) return String.format("%d s", seconds);
		long minutes = seconds/60;
		return minutesToString(minutes, seconds-minutes*60, showDays);
	}
	
	public static String minutesToString(long minutes) {
		return minutesToString(minutes, false);
	}
	
	public static String minutesToString(long minutes, boolean showDays) {
		long hours = minutes/60;
		return hoursToString(hours, minutes-hours*60, showDays);
	}

	private static String minutesToString(long minutes, long seconds, boolean showDays) {
		long hours = minutes/60;
		return hoursToString(hours, minutes-hours*60, seconds, showDays);
	}

	private static String hoursToString(long hours, long minutes, boolean showDays) {
		if (!showDays || (hours < 24)) return String.format("%d:%02d", hours, minutes);
		long days = hours/24;
		return daysToString(days, hours-days*24, minutes);
	}

	private static String hoursToString(long hours, long minutes, long seconds, boolean showDays) {
		if (!showDays || (hours < 24)) return String.format("%d:%02d:%02d", hours, minutes, seconds);
		long days = hours/24;
		return daysToString(days, hours-days*24, minutes, seconds);
	}

	private static String daysToString(long days, long hours, long minutes) {
		return String.format("%d days, %d:%02d", days, hours, minutes);
	}

	private static String daysToString(long days, long hours, long minutes, long seconds) {
		return String.format("%d days, %d:%02d:%02d", days, hours, minutes, seconds);
	}
}

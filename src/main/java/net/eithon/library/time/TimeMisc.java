package net.eithon.library.time;

public class TimeMisc {
	public static long secondsToTicks(double seconds)
	{
		return Math.round(20*seconds);
	}

	public static double ticksToSeconds(long ticks)
	{
		return ticks/20.0;
	}
	
	public static String secondsToString(long seconds) {
		if (seconds < 60) return String.format("%d s");
		long minutes = seconds/60;
		return minutesToString(minutes, seconds-minutes*60);
	}
	
	public static String minutesToString(long minutes) {
		if (minutes < 60) return String.format("%d minutes");
		long hours = minutes/60;
		return hoursToString(hours, minutes-hours*60);
	}

	private static String minutesToString(long minutes, long seconds) {
		long hours = minutes/60;
		return hoursToString(hours, minutes-hours*60, seconds);
	}

	private static String hoursToString(long hours, long minutes) {
		if (hours < 24) return String.format("%d:%d", hours, minutes);
		long days = hours/24;
		return daysToString(days, hours-days*24, minutes);
	}

	private static String hoursToString(long hours, long minutes, long seconds) {
		if (hours < 24) return String.format("%d:%d:%d", hours, minutes, seconds);
		long days = hours/24;
		return daysToString(days, hours-days*24, minutes, seconds);
	}

	private static String daysToString(long days, long hours, long minutes) {
		return String.format("%d days, %d:%d", days, hours, minutes);
	}

	private static String daysToString(long days, long hours, long minutes, long seconds) {
		return String.format("%d days, %d:%d:%d", days, hours, minutes, seconds);
	}
}

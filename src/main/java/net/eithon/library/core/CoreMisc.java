package net.eithon.library.core;

import java.util.IllegalFormatException;

public class CoreMisc {
	public static String safeFormat(String format, Object... args)
	{
		String message = null;
		try {
			message = String.format(format, args);
		} catch (IllegalFormatException e) {
			String errorMessage = e.getMessage();
			message = String.format("String.format(\"%s\", %d arguments) throw IllegalFormatException: %s",
					format, args.length, errorMessage);
		}
		return message;
	}

	public static String arrayToString(String[] stringList) {
		String s = "[";
		for (String string : stringList) {
			if (!s.equalsIgnoreCase("[")) s += ", ";
			s += string;
		}
		s +="]";
		return s;
	}

	public static String arrayToString(Integer[] integerList) {
		String s = "[";
		for (Integer integer : integerList) {
			if (!s.equalsIgnoreCase("[")) s += ", ";
			s += integer.toString();
		}
		s +="]";
		return s;
	}
}

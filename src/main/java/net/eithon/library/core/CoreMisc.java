package net.eithon.library.core;

import java.util.Collection;
import java.util.IllegalFormatException;

public class CoreMisc {
	public static String safeFormat(String format, Object... args)
	{
		String message = null;
		try {
			message = String.format(format, args);
		} catch (IllegalFormatException e) {
			String errorMessage = e.getMessage();
			message = String.format("String.format(\"%s\", %d arguments) throw IllegalFormatException: %s\n",
					format, args.length, errorMessage);
			e.printStackTrace();
		}
		return message;
	}

	public static boolean isStringInCollectionIgnoreCase(String string, Collection<String> stringList) {
		for (String item : stringList) {
			if (item.equalsIgnoreCase(string)) return true;
		}
		return false;
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

	public static String collectionToString(Collection<String> stringList) {
		return arrayToString(stringList.toArray(new String[0]));
	}
}

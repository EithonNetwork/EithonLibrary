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
}

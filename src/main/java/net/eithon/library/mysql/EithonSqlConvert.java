package net.eithon.library.mysql;

import java.time.LocalDateTime;

public class EithonSqlConvert {
	public static LocalDateTime toLocalDateTime(java.sql.Date source) {
		return source.toLocalDate().atStartOfDay();
	}
	
	public static LocalDateTime toLocalDateTime(java.sql.Timestamp source) {
		return source.toLocalDateTime();
	}
	
	public static java.sql.Date toSqlDate(LocalDateTime source) {
		return java.sql.Date.valueOf(source.toLocalDate());
	}
	
	public static java.sql.Timestamp toSqlTimestamp(LocalDateTime source) {
		return java.sql.Timestamp.valueOf(source);
	}
}

package com.example.astroweather;

import com.astrocalculator.AstroDateTime;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {
	public static int getOffsetHours(TimeZone timeZone) {
		return (int) TimeUnit.MILLISECONDS.toHours(timeZone.getOffset(System.currentTimeMillis()));
	}


	public static String formatDate(AstroDateTime date_time) {
		String date = String.format("%02d/%02d/%04d", date_time.getDay(), date_time.getMonth(), date_time.getYear());
		return date;
	}


	public static String formatTime(AstroDateTime date_time) {
		String time = String.format("%02d:%02d", date_time.getHour(), date_time.getMinute());
		return time;
	}
}

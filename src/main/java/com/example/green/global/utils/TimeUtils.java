package com.example.green.global.utils;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class TimeUtils {

	private final Clock clock;

	public TimeUtils() {
		this(Clock.systemDefaultZone());
	}

	public TimeUtils(Clock clock) {
		this.clock = clock;
	}

	public Date getDate() {
		return Date.from(Instant.now(clock));
	}

	public Date getDate(long dateTime) {
		return new Date(dateTime);
	}

	public long getCurrentTimeMillis() {
		return Instant.now(clock).toEpochMilli();
	}

	public String getFormattedDate(String format) {
		return new SimpleDateFormat(format).format(getDate());
	}
}

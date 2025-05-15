package com.example.green.global.utils.base;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.example.green.global.utils.DateUtils;

@Component
public class DefaultDateUtils implements DateUtils {

	private final Clock clock;

	public DefaultDateUtils() {
		this(Clock.systemDefaultZone());
	}

	public DefaultDateUtils(Clock clock) {
		this.clock = clock;
	}

	@Override
	public Date getDate() {
		return Date.from(Instant.now(clock));
	}

	@Override
	public Date getDate(long dateTime) {
		return new Date(dateTime);
	}
}

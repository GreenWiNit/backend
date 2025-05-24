package com.example.green.unit.utils;

import static org.assertj.core.api.Assertions.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.example.green.global.utils.TimeUtils;

class TimeUtilsTest {

	@Test
	void 현재_시간을_가져온다() {
		// given
		Instant fixedInstant = Instant.now();
		Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
		TimeUtils timeUtils = new TimeUtils(fixedClock);

		// when
		Date result = timeUtils.getDate();

		// then
		Date expected = Date.from(fixedInstant);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	void 타임스탬프로_Date_정보를_가져온다() {
		// given
		Date now = new Date();
		long time = now.getTime();
		TimeUtils timeUtils = new TimeUtils();

		// when
		Date result = timeUtils.getDate(time);

		// then
		assertThat(result).isEqualTo(now);
	}

	@Test
	void 현재_시간을_밀리초로_가져온다() {
		// given
		Instant fixedInstant = Instant.now();
		Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
		TimeUtils timeUtils = new TimeUtils(fixedClock);

		// when
		long result = timeUtils.getCurrentTimeMillis();

		// then
		long expected = fixedInstant.toEpochMilli();
		assertThat(result).isEqualTo(expected);
	}

	@Test
	void 현재_시간을_원하는_포맷으로_가져온다() {
		// given
		Instant fixedInstant = Instant.ofEpochMilli(1620000000000L);
		Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
		TimeUtils timeUtils = new TimeUtils(fixedClock);

		// when
		String result = timeUtils.getFormattedDate("yyyyMMdd");

		// then
		assertThat(result).isEqualTo("20210503");
	}
}
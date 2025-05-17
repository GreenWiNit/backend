package com.example.green.global.utils.base;

import static org.assertj.core.api.Assertions.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.example.green.global.utils.DateUtils;

class DefaultDateUtilsTest {

	@Test
	void 현재_시간을_가져온다() {
		// given
		Instant fixedInstant = Instant.now();
		Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
		DateUtils dateUtils = new DefaultDateUtils(fixedClock);

		// when
		Date result = dateUtils.getDate();

		// then
		Date expected = Date.from(fixedInstant);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	void 타임스탬프로_Date_정보를_가져온다() {
		// given
		Date now = new Date();
		long time = now.getTime();
		DateUtils dateUtils = new DefaultDateUtils();

		// when
		Date result = dateUtils.getDate(time);

		// then
		assertThat(result).isEqualTo(now);
	}

}
package com.example.green.unit.utils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.green.global.utils.IdUtils;

class IdUtilsTest {

	@Test
	void 고유한_ID를_특정_길이만큼_추출한다() {
		// given
		String fixedUuid = "abcd12345";
		IdUtils idUtils = new IdUtils(() -> fixedUuid);

		// when
		String result = idUtils.generateUniqueId(4);

		// then
		assertThat(result).isEqualTo("abcd");
	}

	@Test
	void 고유한_ID는_매번_다른_값을_생성한다() {
		// given
		IdUtils idUtils = new IdUtils();

		// when
		String firstUuid = idUtils.generateUniqueId(8);
		String secondUuid = idUtils.generateUniqueId(8);

		// then
		assertThat(firstUuid).isNotEqualTo(secondUuid);
	}
}
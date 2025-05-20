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
		String expected = fixedUuid.substring(0, 4);
		assertThat(result).isEqualTo(expected);
	}
}
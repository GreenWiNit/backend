package com.example.green.global.utils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.green.global.error.exception.BusinessException;

class UlidUtilsTest {

	@Test
	@DisplayName("ULID 생성 성공")
	void generate_Success() {
		// when
		String ulid = UlidUtils.generate();

		// then
		assertThat(ulid).hasSize(26);
		assertThat(UlidUtils.isValid(ulid)).isTrue();
	}

	@Test
	@DisplayName("타임스탬프로 ULID 생성 성공")
	void generateWithTimestamp_Success() {
		// given
		long timestamp = System.currentTimeMillis();

		// when
		String ulid = UlidUtils.generate(timestamp);

		// then
		assertThat(ulid).hasSize(26);
		assertThat(UlidUtils.isValid(ulid)).isTrue();
		assertThat(UlidUtils.extractTimestamp(ulid)).isEqualTo(timestamp);
	}

	@Test
	@DisplayName("ULID에서 타임스탬프 추출 성공")
	void extractTimestamp_Success() {
		// given
		long originalTimestamp = System.currentTimeMillis();
		String ulid = UlidUtils.generate(originalTimestamp);

		// when
		long extractedTimestamp = UlidUtils.extractTimestamp(ulid);

		// then
		assertThat(extractedTimestamp).isEqualTo(originalTimestamp);
	}

	@Test
	@DisplayName("유효한 ULID 검증 성공")
	void isValid_ValidUlid_ReturnsTrue() {
		// given
		String ulid = UlidUtils.generate();

		// when & then
		assertThat(UlidUtils.isValid(ulid)).isTrue();
	}

	@Test
	@DisplayName("잘못된 길이의 ULID 검증 실패")
	void isValid_InvalidLength_ReturnsFalse() {
		// given
		String shortUlid = "123456789";
		String longUlid = "1234567890123456789012345678901234567890";

		// when & then
		assertThat(UlidUtils.isValid(shortUlid)).isFalse();
		assertThat(UlidUtils.isValid(longUlid)).isFalse();
		assertThat(UlidUtils.isValid(null)).isFalse();
	}

	@Test
	@DisplayName("잘못된 문자가 포함된 ULID 검증 실패")
	void isValid_InvalidCharacter_ReturnsFalse() {
		// given - I, L, O, U는 ULID에 사용되지 않음
		String invalidUlid = "01BX5ZZKBKACTAV9WEVGMMVWXU"; // U 포함

		// when & then
		assertThat(UlidUtils.isValid(invalidUlid)).isFalse();
	}

	@Test
	@DisplayName("null ULID에서 타임스탬프 추출 시 예외 발생")
	void extractTimestamp_NullUlid_ThrowsException() {
		// when & then
		assertThatThrownBy(() -> UlidUtils.extractTimestamp(null))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("잘못된 길이의 ULID에서 타임스탬프 추출 시 예외 발생")
	void extractTimestamp_InvalidLength_ThrowsException() {
		// given
		String invalidUlid = "123";

		// when & then
		assertThatThrownBy(() -> UlidUtils.extractTimestamp(invalidUlid))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("동일한 타임스탬프로 생성된 ULID들은 시간 부분은 같지만 랜덤 부분은 다름")
	void generate_SameTimestamp_DifferentRandomPart() {
		// given
		long timestamp = System.currentTimeMillis();

		// when
		String ulid1 = UlidUtils.generate(timestamp);
		String ulid2 = UlidUtils.generate(timestamp);

		// then
		assertThat(ulid1).isNotEqualTo(ulid2);
		// 시간 부분 (앞 10자리)은 동일
		assertThat(ulid1.substring(0, 10)).isEqualTo(ulid2.substring(0, 10));
		// 랜덤 부분 (뒤 16자리)은 다름
		assertThat(ulid1.substring(10)).isNotEqualTo(ulid2.substring(10));
	}

	@Test
	@DisplayName("시간순으로 생성된 ULID들은 정렬 가능")
	void generate_ChronologicalOrder_Sortable() throws InterruptedException {
		// given
		String ulid1 = UlidUtils.generate();
		Thread.sleep(1); // 시간 차이 보장
		String ulid2 = UlidUtils.generate();

		// when & then
		assertThat(ulid1.compareTo(ulid2)).isLessThan(0);
	}
} 
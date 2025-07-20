package com.example.green.domain.challengecert.dto;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ChallengeCertificationCreateResponseDtoTest {

	@Test
	void Builder를_사용하여_정상적으로_생성된다() {
		// given & when
		ChallengeCertificationCreateResponseDto response = ChallengeCertificationCreateResponseDto.builder()
			.certificationId(100L)
			.build();

		// then
		assertThat(response.certificationId()).isEqualTo(100L);
	}

	@Test
	void certificationId가_null인_경우에도_생성된다() {
		// given & when
		ChallengeCertificationCreateResponseDto response = ChallengeCertificationCreateResponseDto.builder()
			.certificationId(null)
			.build();

		// then
		assertThat(response.certificationId()).isNull();
	}

	@Test
	void Record의_equals와_hashCode가_정상적으로_작동한다() {
		// given
		ChallengeCertificationCreateResponseDto response1 = ChallengeCertificationCreateResponseDto.builder()
			.certificationId(100L)
			.build();

		ChallengeCertificationCreateResponseDto response2 = ChallengeCertificationCreateResponseDto.builder()
			.certificationId(100L)
			.build();

		ChallengeCertificationCreateResponseDto response3 = ChallengeCertificationCreateResponseDto.builder()
			.certificationId(200L)
			.build();

		// when & then
		assertThat(response1).isEqualTo(response2);
		assertThat(response1).isNotEqualTo(response3);
		assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
		assertThat(response1.hashCode()).isNotEqualTo(response3.hashCode());
	}

	@Test
	void toString이_정상적으로_작동한다() {
		// given
		ChallengeCertificationCreateResponseDto response = ChallengeCertificationCreateResponseDto.builder()
			.certificationId(100L)
			.build();

		// when
		String toString = response.toString();

		// then
		assertThat(toString).contains("ChallengeCertificationCreateResponseDto");
		assertThat(toString).contains("certificationId=100");
	}
} 
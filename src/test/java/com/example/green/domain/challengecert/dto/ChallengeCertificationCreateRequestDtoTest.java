package com.example.green.domain.challengecert.dto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ChallengeCertificationCreateRequestDtoTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void 모든_필드가_유효한_경우_검증에_성공한다() {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl("https://example.com/image.jpg")
			.certificationReview("오늘도 열심히 운동했습니다!")
			.build();

		// when
		Set<ConstraintViolation<ChallengeCertificationCreateRequestDto>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	void 인증_날짜가_null인_경우_검증에_실패한다() {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(null)
			.certificationImageUrl("https://example.com/image.jpg")
			.certificationReview("테스트 후기")
			.build();

		// when
		Set<ConstraintViolation<ChallengeCertificationCreateRequestDto>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("인증 날짜는 필수값입니다.");
	}

	@Test
	void 인증_이미지_URL이_null인_경우_검증에_실패한다() {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(null)
			.certificationReview("테스트 후기")
			.build();

		// when
		Set<ConstraintViolation<ChallengeCertificationCreateRequestDto>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).contains("인증 이미지 URL은 필수값입니다");
	}

	@Test
	void 인증_이미지_URL이_빈_문자열인_경우_검증에_실패한다() {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl("")
			.certificationReview("테스트 후기")
			.build();

		// when
		Set<ConstraintViolation<ChallengeCertificationCreateRequestDto>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).contains("인증 이미지 URL은 필수값입니다");
	}

	@Test
	void 인증_이미지_URL이_공백만_있는_경우_검증에_실패한다() {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl("   ")
			.certificationReview("테스트 후기")
			.build();

		// when
		Set<ConstraintViolation<ChallengeCertificationCreateRequestDto>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).contains("인증 이미지 URL은 필수값입니다");
	}

	@Test
	void 인증_이미지_URL이_500자를_초과하는_경우_검증에_실패한다() {
		// given
		String longUrl = "https://example.com/" + "a".repeat(490); // 19 + 490 = 509자 (500자 초과)
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(longUrl)
			.certificationReview("테스트 후기")
			.build();

		// when
		Set<ConstraintViolation<ChallengeCertificationCreateRequestDto>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("인증 이미지 URL은 500자 이하여야 합니다.");
	}

	@Test
	void 인증_후기가_45자를_초과하는_경우_검증에_실패한다() {
		// given
		String longReview = "a".repeat(46); // 45자 초과
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl("https://example.com/image.jpg")
			.certificationReview(longReview)
			.build();

		// when
		Set<ConstraintViolation<ChallengeCertificationCreateRequestDto>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("인증 후기는 최대 45자까지 입력 가능합니다.");
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 10, 30, 45})
	void 인증_후기가_45자_이하인_경우_검증에_성공한다(int length) {
		// given
		String validReview = "a".repeat(length);
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl("https://example.com/image.jpg")
			.certificationReview(validReview)
			.build();

		// when
		Set<ConstraintViolation<ChallengeCertificationCreateRequestDto>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	void 인증_후기가_null인_경우_검증에_성공한다() {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl("https://example.com/image.jpg")
			.certificationReview(null)
			.build();

		// when
		Set<ConstraintViolation<ChallengeCertificationCreateRequestDto>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	void 인증_후기가_빈_문자열인_경우_검증에_성공한다() {
		// given
		ChallengeCertificationCreateRequestDto request = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl("https://example.com/image.jpg")
			.certificationReview("")
			.build();

		// when
		Set<ConstraintViolation<ChallengeCertificationCreateRequestDto>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}
}

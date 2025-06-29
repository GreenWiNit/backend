package com.example.green.global.utils;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

class EntityValidatorTest {

	@Test
	void NULL_데이터를_검증한다() {
		assertThatThrownBy(() -> EntityValidator.validateNullData(null, "message"))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
	}

	@Test
	void NULL_체크시_데이터가_존재하면_예외가_발생하지_않는다() {
		assertThatCode(() -> EntityValidator.validateNullData(new Object(), "message"))
			.doesNotThrowAnyException();
	}

	@ParameterizedTest
	@ValueSource(longs = {-1L, 0L})
	@NullSource
	void 엔터티_자동_증가_아이디_값을_검증한다(Long id) {
		assertThatThrownBy(() -> EntityValidator.validateAutoIncrementId(id, "message"))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
	}

	@Test
	void 엔터티_자동_증가_아이디가_유효하면_예외가_발생하지_않는다() {
		assertThatCode(() -> EntityValidator.validateAutoIncrementId(1L, "message"))
			.doesNotThrowAnyException();
	}

	@ParameterizedTest
	@ValueSource(strings = {"  ", ""})
	@NullSource
	void 유효한_문자열을_검증한다(String string) {
		assertThatThrownBy(() -> EntityValidator.validateEmptyString(string, "message"))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
	}

	@Test
	void 빈_문자열이_아닌_유효한_문자라면_예외가_발생하지_않는다() {
		assertThatCode(() -> EntityValidator.validateEmptyString("String", "message"))
			.doesNotThrowAnyException();
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 유효한_리스트를_검증한다(List<?> datas) {
		assertThatThrownBy(() -> EntityValidator.validateEmptyList(datas, "message"))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
	}

	@Test
	void 리스트가_1개_이상_존재하면_예외가_발생하지_않는다() {
		assertThatCode(() -> EntityValidator.validateEmptyList(List.of(new Object()), "message"))
			.doesNotThrowAnyException();
	}
}
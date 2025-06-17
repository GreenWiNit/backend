package com.example.green.global.excel.style;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.example.green.global.excel.exception.ExcelException;
import com.example.green.global.excel.exception.ExcelExceptionMessage;

class ExcelFieldTest {

	@Test
	void 엑셀_필드_정보로_필드_값을_생성할_수_있다() {
		// given
		String fieldName = "test";
		BackGroundColor backgroundColor = BackGroundColor.WHITE;
		FieldFormat format = FieldFormat.NUMBER;

		// when
		ExcelField field = ExcelField.of(fieldName, backgroundColor, format);

		// then
		assertThat(field.getName()).isEqualTo(fieldName);
		assertThat(field.getBackGroundColor()).isEqualTo(backgroundColor);
		assertThat(field.getFormat()).isEqualTo(format);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 필드명이_비어있으면_예외가_발생한다(String fieldName) {
		// given
		BackGroundColor backgroundColor = BackGroundColor.WHITE;
		FieldFormat format = FieldFormat.NUMBER;

		// when & then
		assertThatThrownBy(() -> ExcelField.of(fieldName, backgroundColor, format))
			.isInstanceOf(ExcelException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
	}

	@Test
	void 필드_배경색이_null이면_예외가_발생한다() {
		// given
		String fieldName = "test";
		BackGroundColor backgroundColor = null;
		FieldFormat format = FieldFormat.NUMBER;

		// when & then
		assertThatThrownBy(() -> ExcelField.of(fieldName, backgroundColor, format))
			.isInstanceOf(ExcelException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
	}

	@Test
	void 필드_형식이_null이면_예외가_발생한다() {
		// given
		String fieldName = "test";
		BackGroundColor backgroundColor = BackGroundColor.LIGHT_GRAY;
		FieldFormat format = null;

		// when & then
		assertThatThrownBy(() -> ExcelField.of(fieldName, backgroundColor, format))
			.isInstanceOf(ExcelException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
	}
}
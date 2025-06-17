package com.example.green.global.excel.style;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.example.green.global.excel.exception.ExcelException;

class FieldFormatTest {

	@Test
	void of메서드는_유효한_패턴과_타입으로_포맷을_생성한다() {
		// given
		String pattern = "#,##0\"점\"";
		FormatType type = FormatType.NUMERIC;

		// when
		FieldFormat format = FieldFormat.of(pattern, type);

		// then
		assertThat(format.getPattern()).isEqualTo(pattern);
		assertThat(format.isNumericFormat()).isTrue();
	}

	@ParameterizedTest
	@NullAndEmptySource
	void of메서드는_null_또는_빈_패턴을_거부한다(String pattern) {
		// given & when & then
		assertThatThrownBy(() -> FieldFormat.of(pattern, FormatType.NUMERIC))
			.isInstanceOf(ExcelException.class);
	}

	@Test
	void of메서드는_null_타입을_거부한다() {
		// given & when & then
		assertThatThrownBy(() -> FieldFormat.of("#,##0", null))
			.isInstanceOf(ExcelException.class);
	}

	@Test
	void numeric_패턴을_만들_수_있다() {
		// given
		String pattern = "#,##0";

		// when
		FieldFormat format = FieldFormat.ofNumeric(pattern);

		// then
		assertThat(format.isNumericFormat()).isTrue();
		assertThat(format.isTemporalFormat()).isFalse();
		assertThat(format.isTextFormat()).isFalse();
	}

	@Test
	void date_패턴을_만들_수_있다() {
		// given
		String pattern = "yyyy-mm-dd";

		// when
		FieldFormat format = FieldFormat.ofDate(pattern);

		// then
		assertThat(format.isTemporalFormat()).isTrue();
		assertThat(format.isNumericFormat()).isFalse();
		assertThat(format.isTextFormat()).isFalse();
	}

	@Test
	void text_패턴을_만들_수_있다() {
		// given
		String pattern = "@";

		// when
		FieldFormat format = FieldFormat.ofText(pattern);

		// then
		assertThat(format.isTextFormat()).isTrue();
		assertThat(format.isNumericFormat()).isFalse();
		assertThat(format.isTemporalFormat()).isFalse();
	}
}
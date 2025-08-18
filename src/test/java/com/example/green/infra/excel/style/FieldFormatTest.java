package com.example.green.infra.excel.style;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.example.green.infra.excel.exception.ExcelException;

class FieldFormatTest {

	@Test
	void 사용자_지정_형식을_추가할_수_있다() {
		// given
		String pattern = "#,##0\"점\"";

		// when
		FieldFormat format = FieldFormat.of(pattern);

		// then
		assertThat(format.getPattern()).isEqualTo(pattern);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void of메서드는_null_또는_빈_패턴을_거부한다(String pattern) {
		// given & when & then
		assertThatThrownBy(() -> FieldFormat.of(pattern))
			.isInstanceOf(ExcelException.class);
	}
}
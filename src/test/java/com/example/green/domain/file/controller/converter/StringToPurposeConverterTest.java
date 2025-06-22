package com.example.green.domain.file.controller.converter;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.green.domain.file.domain.vo.Purpose;

class StringToPurposeConverterTest {

	private StringToPurposeConverter converter = new StringToPurposeConverter();

	@Test
	void Purpose_value로_매핑한다() {
		Purpose info = converter.convert("info");
		assertThat(info).isEqualTo(Purpose.INFO);
	}

	@Test
	void Purpose_value가_아니라면_예외가_발생한다() {
		assertThatThrownBy(() -> converter.convert("INFO"))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
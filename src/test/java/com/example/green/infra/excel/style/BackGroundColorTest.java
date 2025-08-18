package com.example.green.infra.excel.style;

import static com.example.green.infra.excel.exception.ExcelExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.infra.excel.exception.ExcelException;

class BackGroundColorTest {

	@ParameterizedTest
	@CsvSource({"255,255,255", "0,0,0", "128,128,128"})
	void rgb값은_0부터_255의_색상_정보로_구성된다(int red, int green, int blue) {
		// given
		// when
		BackGroundColor result = BackGroundColor.of(red, green, blue);

		// then
		byte[] expected = {(byte)red, (byte)green, (byte)blue};
		assertThat(result.toRgb()).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
		"256, 255, 255",  // red 초과
		"255, 256, 255",  // green 초과
		"255, 255, 256",  // blue 초과
		"-1, 0, 0",       // red 미만
		"0, -1, 0",       // green 미만
		"0, 0, -1"        // blue 미만
	})
	void rgb값의_범위가_벗어나면_예외가_발생한다(int red, int green, int blue) {
		// given
		// when & then
		assertThatThrownBy(() -> BackGroundColor.of(red, green, blue))
			.isInstanceOf(ExcelException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", EXCEL_GENERATION_FAILED);
	}

	@ParameterizedTest
	@CsvSource({
		"#FFFFFF, 255, 255, 255",  // 대문자 포함 + 마지막 색상
		"#000000, 0, 0, 0",        // 숫자로 구성 + 처음 색상
		"#ff5733, 255, 87, 51",    // 소문자 포함
		"#A1B2C3, 161, 178, 195"   // 대소문자 혼합
	})
	void 헥사_컬러_값이_주어지면_색상_정보를_반환한다(String hexColor, int red, int green, int blue) {
		// given
		// when
		BackGroundColor result = BackGroundColor.of(hexColor);

		// then
		byte[] expected = {(byte)red, (byte)green, (byte)blue};
		assertThat(result.toRgb()).isEqualTo(expected);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"FFFFFF",      // # 없음
		"#FFF",        // 길이 부족
		"#FFFFFFF",    // 길이 초과
		"#GGGGGG",     // 범위 벗어남
		"#FF-000",     // 특수문자
		"",            // 빈 문자열
		"#",           // # 만 있음
	})
	void 잘못된_헥사_컬러_코드라면_예외가_발생한다(String hexColor) {
		// given
		// when & then
		assertThatThrownBy(() -> BackGroundColor.of(hexColor))
			.isInstanceOf(ExcelException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", EXCEL_GENERATION_FAILED);
	}
}
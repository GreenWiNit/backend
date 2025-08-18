package com.example.green.infra.excel.style;

import java.util.regex.Pattern;

import com.example.green.infra.excel.exception.ExcelException;
import com.example.green.infra.excel.exception.ExcelExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"red", "green", "blue"})
@Slf4j
public class BackGroundColor {

	public static final BackGroundColor WHITE = BackGroundColor.of(255, 255, 255);
	public static final BackGroundColor LIGHT_GRAY = BackGroundColor.of(211, 211, 211);

	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");

	private final int red;
	private final int green;
	private final int blue;

	public static BackGroundColor of(int red, int green, int blue) {
		validateRgbRange(red);
		validateRgbRange(green);
		validateRgbRange(blue);
		return new BackGroundColor(red, green, blue);
	}

	public static BackGroundColor of(String hexColorCode) {
		String hexCode = parseHexCode(hexColorCode);
		int red = Integer.parseInt(hexCode.substring(0, 2), 16);
		int green = Integer.parseInt(hexCode.substring(2, 4), 16);
		int blue = Integer.parseInt(hexCode.substring(4, 6), 16);
		return new BackGroundColor(red, green, blue);
	}

	private static String parseHexCode(String hexColorCode) {
		boolean isHexCode = HEX_COLOR_PATTERN.matcher(hexColorCode).matches();
		if (!isHexCode) {
			log.error("잘못된 헥사 코드 값 입니다. require: #000000 ~ #FFFFFF");
			throw new ExcelException(ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
		}
		return hexColorCode.substring(1);
	}

	private static void validateRgbRange(int color) {
		if (color < 0 || color > 255) {
			log.error("잘못된 rgb 값 입니다. require: 0 ~ 255");
			throw new ExcelException(ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
		}
	}

	public byte[] toRgb() {
		return new byte[] {(byte)red, (byte)green, (byte)blue};
	}
}

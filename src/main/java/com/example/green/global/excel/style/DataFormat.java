package com.example.green.global.excel.style;

import com.example.green.global.excel.exception.ExcelException;
import com.example.green.global.excel.exception.ExcelExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DataFormat {
	public static final DataFormat TEXT = new DataFormat("@", FormatType.TEXT);
	public static final DataFormat NUMBER = new DataFormat("#,##0", FormatType.NUMERIC);
	public static final DataFormat DECIMAL = new DataFormat("#,##0.00", FormatType.NUMERIC);
	public static final DataFormat PERCENTAGE = new DataFormat("0.00%", FormatType.NUMERIC);
	public static final DataFormat POINT = new DataFormat("#,##0\"p\"", FormatType.NUMERIC);
	public static final DataFormat DATE = new DataFormat("yyyy-mm-dd", FormatType.DATE);
	public static final DataFormat DATETIME = new DataFormat("yyyy-mm-dd hh:mm:ss", FormatType.DATE);

	@Getter
	private final String pattern;
	private final FormatType formatType;

	public static DataFormat of(String pattern, FormatType formatType) {
		validateEmptyPattern(pattern);
		if (formatType == null) {
			log.error("excel field format 형식이 null 입니다.");
			throw new ExcelException(ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
		}
		return new DataFormat(pattern, formatType);
	}

	public static DataFormat ofText(String pattern) {
		return of(pattern, FormatType.TEXT);
	}

	public static DataFormat ofNumeric(String pattern) {
		return of(pattern, FormatType.NUMERIC);
	}

	public static DataFormat ofDate(String pattern) {
		return of(pattern, FormatType.DATE);
	}

	private static void validateEmptyPattern(String pattern) {
		if (pattern == null || pattern.isEmpty()) {
			log.error("excel field pattern 정보가 비어있습니다.");
			throw new ExcelException(ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
		}
	}

	public boolean isNumericFormat() {
		return formatType == FormatType.NUMERIC;
	}

	public boolean isDateFormat() {
		return formatType == FormatType.DATE;
	}

	public boolean isTextFormat() {
		return formatType == FormatType.TEXT;
	}
}

package com.example.green.global.excel.style;

import com.example.green.global.excel.exception.ExcelException;
import com.example.green.global.excel.exception.ExcelExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class FieldFormat {
	public static final FieldFormat TEXT = new FieldFormat("@", FormatType.TEXT);
	public static final FieldFormat NUMBER = new FieldFormat("#,##0", FormatType.NUMERIC);
	public static final FieldFormat DECIMAL = new FieldFormat("#,##0.00", FormatType.NUMERIC);
	public static final FieldFormat PERCENTAGE = new FieldFormat("0.00%", FormatType.NUMERIC);
	public static final FieldFormat POINT = new FieldFormat("#,##0\"p\"", FormatType.NUMERIC);
	public static final FieldFormat DATE = new FieldFormat("yyyy-mm-dd", FormatType.TEMPORAL);
	public static final FieldFormat DATETIME = new FieldFormat("yyyy-mm-dd hh:mm:ss", FormatType.TEMPORAL);

	@Getter
	private final String pattern;
	private final FormatType formatType;

	public static FieldFormat of(String pattern, FormatType formatType) {
		validateEmptyPattern(pattern);
		if (formatType == null) {
			log.error("excel field format 형식이 null 입니다.");
			throw new ExcelException(ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
		}
		return new FieldFormat(pattern, formatType);
	}

	public static FieldFormat ofText(String pattern) {
		return of(pattern, FormatType.TEXT);
	}

	public static FieldFormat ofNumeric(String pattern) {
		return of(pattern, FormatType.NUMERIC);
	}

	public static FieldFormat ofDate(String pattern) {
		return of(pattern, FormatType.TEMPORAL);
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

	public boolean isTemporalFormat() {
		return formatType == FormatType.TEMPORAL;
	}

	public boolean isTextFormat() {
		return formatType == FormatType.TEXT;
	}
}

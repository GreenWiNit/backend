package com.example.green.global.excel.style;

import com.example.green.global.excel.exception.ExcelException;
import com.example.green.global.excel.exception.ExcelExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class FieldFormat {
	public static final FieldFormat TEXT = new FieldFormat("@");
	public static final FieldFormat NUMBER = new FieldFormat("#,##0");
	public static final FieldFormat DECIMAL = new FieldFormat("#,##0.00");
	public static final FieldFormat PERCENTAGE = new FieldFormat("0.00%");
	public static final FieldFormat POINT = new FieldFormat("#,##0\"p\"");
	public static final FieldFormat DATE = new FieldFormat("yyyy-mm-dd");
	public static final FieldFormat DATETIME = new FieldFormat("yyyy-mm-dd hh:mm:ss");

	@Getter
	private final String pattern;

	public static FieldFormat of(String pattern) {
		if (pattern == null || pattern.isEmpty()) {
			log.error("excel field pattern 정보가 비어있습니다.");
			throw new ExcelException(ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
		}
		return new FieldFormat(pattern);
	}
}

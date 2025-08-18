package com.example.green.infra.excel.style;

import com.example.green.infra.excel.exception.ExcelException;
import com.example.green.infra.excel.exception.ExcelExceptionMessage;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ExcelField {

	private final String name;
	private final BackGroundColor backGroundColor;
	private final FieldFormat format;

	public static ExcelField of(String name, BackGroundColor backGroundColor, FieldFormat format) {
		if (name == null || name.isEmpty()) {
			log.error("excel field 명이 비어있습니다.");
			throw new ExcelException(ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
		}
		if (format == null) {
			log.error("excel field 형식이 null 입니다.");
			throw new ExcelException(ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
		}
		if (backGroundColor == null) {
			log.error("excel field 배경색이 null 입니다.");
			throw new ExcelException(ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
		}
		return new ExcelField(name, backGroundColor, format);
	}
}

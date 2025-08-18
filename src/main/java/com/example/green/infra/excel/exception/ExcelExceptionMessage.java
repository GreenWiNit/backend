package com.example.green.infra.excel.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExcelExceptionMessage implements ExceptionMessage {

	EXCEL_GENERATION_FAILED(INTERNAL_SERVER_ERROR, "엑셀 파일 생성에 실패했습니다."),
	EMPTY_DATA(INTERNAL_SERVER_ERROR, "엑셀로 추출하려는 데이터가 비어있습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}

package com.example.green.domain.file.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileExceptionMessage implements ExceptionMessage {

	REQUIRED_IMAGE_FILE(BAD_REQUEST, "이미지 파일은 필수 데이터입니다."),
	EMPTY_IMAGE_FILE_NAME(BAD_REQUEST, "이미지 파일의 이름이 존재하지 않습니다."),
	INVALID_IMAGE_TYPE(BAD_REQUEST, "이미지 유형의 파일이 아닙니다."),
	OVER_MAX_IMAGE_SIZE(BAD_REQUEST, "최대 이미지 파일 크기를 초과했습니다."),
	IMAGE_UPLOAD_FAILED(INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}

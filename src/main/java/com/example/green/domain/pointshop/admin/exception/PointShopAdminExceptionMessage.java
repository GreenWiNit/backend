package com.example.green.domain.pointshop.admin.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PointShopAdminExceptionMessage implements ExceptionMessage {

	NOT_FOUND_CATEGORY(NOT_FOUND, "카테고리를 찾을 수가 없습니다"),
	NOT_FOUND_PRODUCT(NOT_FOUND, "등록된 상품 / 아이템을 찾을 수 없습니다");

	private final HttpStatus httpStatus;
	private final String message;

}

package com.example.green.domain.pointshop.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PointProductExceptionMessage implements ExceptionMessage {

	INVALID_PRODUCT_CODE(BAD_REQUEST, "상품 코드는 PRD-AA-000 형식입니다."),
	INVALID_PRODUCT_PRICE(BAD_REQUEST, "상품 가격은 0원 이상이어야 합니다."),
	INVALID_PRODUCT_STOCK_CREATION(BAD_REQUEST, "상품 생성 시 상품 재고는 1개 이상이어야 합니다."),
	INVALID_PRODUCT_STOCK(BAD_REQUEST, "상품 재고는 0개 이상이어야 합니다."),
	INVALID_PRODUCT_NAME(BAD_REQUEST, "상품명은 2글자 ~ 15글자로 구성되어야 합니다."),
	INVALID_PRODUCT_THUMBNAIL(BAD_REQUEST, "상품 썸네일 주소가 잘못되었습니다."),
	INVALID_PRODUCT_DESCRIPTION(BAD_REQUEST, "상품 설명은 최대 100글자로 구성되어야 합니다."),
	OUT_OF_PRODUCT_STOCK(BAD_REQUEST, "상품 재고가 부족합니다."),
	INVALID_SEARCH_KEYWORD(BAD_REQUEST, "상품 검색 내용은 최소 2글자여야 합니다..");

	public static final String REQUIRED_BASIC_INFO = "상품 기본 정보가 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_CODE = "상품 코드 정보가 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_NAME = "상품 이름 정보가 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_DESCRIPTION = "상품 설명 정보가 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_MEDIA = "상품 미디어 정보가 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_PRICE = "상품 가격이 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_STOCK = "상품 재고가 null 값 입니다. 파라미터를 확인해주세요.";

	private final HttpStatus httpStatus;
	private final String message;
}

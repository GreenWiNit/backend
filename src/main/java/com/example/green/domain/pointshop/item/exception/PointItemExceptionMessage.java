package com.example.green.domain.pointshop.item.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PointItemExceptionMessage implements ExceptionMessage {

	NOT_FOUND_USER(NOT_FOUND, "사용자를 찾을 수 없습니다"),
	INVALID_ITEM_THUMBNAIL(BAD_REQUEST, "아이템 썸네일 주소가 잘못되었습니다."),
	INVALID_ITEM_PRICE(BAD_REQUEST, "아이템 가격은 0원 이상이어야 합니다."),
	INVALID_ITEM_CODE(BAD_REQUEST, "아이템 코드는 ITM-AA-000 형식입니다."),
	INVALID_ITEM_NAME(BAD_REQUEST, "아이템명은 2글자 ~ 15글자로 구성되어야 합니다."),
	INVALID_ITEM_DESCRIPTION(BAD_REQUEST, "아이템 설명은 최대 100글자로 구성되어야 합니다."),
	EXISTS_ITEM_CODE(CONFLICT, "중복된 아이템 상품 코드가 존재합니다."),
	NOT_FOUND_ITEM(NOT_FOUND, "아이템을 찾을 수 없습니다."),
	INVALID_ITEM_STOCK(BAD_REQUEST, "아이템 재고는 0개 이상이어야 합니다."),
	INVALID_ITEM_STOCK_CREATION(BAD_REQUEST, "아이템 생성 시 아이템 재고는 1개 이상이어야 합니다."),
	OUT_OF_ITEM_STOCK(BAD_REQUEST, "아이템 재고가 부족합니다."),
	DUPLICATE_POINT_ITEM_CODE(CONFLICT, "중복된 아이템 코드가 존재합니다."),
	NOT_POSSIBLE_BUY_ITEM(BAD_REQUEST, "가지고 있는 포인트가 부족해 해당 아이템을 구매할 수 없습니다");

	public static final String REQUIRED_ITEM_CODE = "아이템 코드 정보가 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_ITEM_MEDIA = "아이템 미디어 정보가 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_ITEM_PRICE = "아이템 가격이 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_ITEM_NAME = "아이템 이름 정보가 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_ITEM_DESCRIPTION = "아이템 설명 정보가 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_ITEM_BASIC_INFO = "아이템 기본 정보가 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_ITEM_STOCK = "아이템 재고가 null 값 입니다. 파라미터를 확인해주세요.";
	public static final String REQUIRED_ENABLE_POINT = "사용 가능한 포인트는 필수 값입니다.";
	public static final String REQUIRED_DECREASE_POINT = "차감 포인트는 필수 값입니다.";
	public static final String REQUIRED_REMAIN_POINT = "남은 포인트는 필수 값입니다.";

	private final HttpStatus httpStatus;
	private final String message;

}

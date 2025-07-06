package com.example.green.domain.point.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PointTransactionResponseMessage implements ResponseMessage {

	MY_POINT_INQUIRY_SUCCESS("내 포인트 조회에 성공했습니다."),
	POINT_TRANSACTION_INQUIRY_SUCCESS("포인트 내역 조회에 성공했습니다.");

	private final String message;
}

package com.example.green.domain.info.controller.api;

import com.example.green.global.api.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO 응답값 만드는 건 엄밀히 말하면 inport 는 아니긴 함 오히려 outport 에 가깝지만 presentation controller 계층 사용
@Getter
@AllArgsConstructor
public enum InfoResponseMessage implements ResponseMessage {
	;

	private final String Message;
}

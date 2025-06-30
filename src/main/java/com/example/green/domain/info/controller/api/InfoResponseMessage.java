package com.example.green.domain.info.controller.api;

import com.example.green.global.api.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InfoResponseMessage implements ResponseMessage {
	;

	private final String Message;
}

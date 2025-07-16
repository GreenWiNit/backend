package com.example.green.domain.pointshop.product.entity.vo;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DisplayStatus {
	DISPLAY("전시"),
	HIDDEN("미전시");

	@JsonValue
	private final String value;
}

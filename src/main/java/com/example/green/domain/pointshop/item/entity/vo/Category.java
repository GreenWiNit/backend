package com.example.green.domain.pointshop.item.entity.vo;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
	PRODUCT("상품"),
	ITEM("아이템");

	@JsonValue
	private final String value;
}

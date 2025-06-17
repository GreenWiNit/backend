package com.example.green.domain.file.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Purpose {

	CHALLENGE("challenge"),
	CHALLENGE_AUTHENTICATION("challenge_authentication"),
	INFO("info"),
	PRODUCT("product");

	private final String value;
}

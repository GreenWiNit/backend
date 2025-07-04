package com.example.green.domain.file.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Purpose {

	CHALLENGE("challenge"),
	CHALLENGE_AUTH("challenge-auth"),
	INFO("info"),
	PRODUCT("product");

	private final String value;

	@Override
	public String toString() {
		return getValue();
	}
}

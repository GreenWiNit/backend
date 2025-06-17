package com.example.green.domain.file.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Purpose {

	CHALLENGE("challenge"),
	SHARE("share"),
	PRODUCT("product");

	private final String value;
}

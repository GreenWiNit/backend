package com.example.green.domain.file.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Purpose {

	PROFILE("profile"),
	INFO("info");

	private final String value;
}

package com.example.green.domain.point.entity.vo;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TargetType {

	EVENT("이벤트"),
	CHALLENGE("챌린지"),
	EXCHANGE("교환");

	@JsonValue
	private final String value;
}

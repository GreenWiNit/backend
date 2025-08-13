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

	public static TargetType from(String type) {
		for (TargetType targetType : TargetType.values()) {
			if (type.equals(targetType.value)) {
				return targetType;
			}
		}
		throw new IllegalStateException("포인트 트랜잭션 타입이 잘못됨");
	}
}

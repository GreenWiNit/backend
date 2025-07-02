package com.example.green.domain.pointshop.entity.pointproduct.vo;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SellingStatus {

	EXCHANGEABLE("교환가능"),
	SOLD_OUT("품절");

	@JsonValue
	private final String value;
}

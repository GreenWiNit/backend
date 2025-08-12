package com.example.green.domain.certification.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CertificationStatus {
	PENDING("인증 요청"),
	APPROVED("지급"),
	REJECTED("미지급");

	@JsonValue
	private final String description;
}

package com.example.green.domain.auth.admin.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminStatus {
	ACTIVE("활성"),
	LOCKED("잠김");

	private final String description;
} 
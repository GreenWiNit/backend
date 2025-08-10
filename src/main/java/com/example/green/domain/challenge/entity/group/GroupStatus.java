package com.example.green.domain.challenge.entity.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 팀 챌린지 그룹 상태 열거형
 */
@RequiredArgsConstructor
@Getter
public enum GroupStatus {
	RECRUITING("모집중"),
	COMPLETED("완료");

	private final String description;
}

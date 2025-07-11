package com.example.green.domain.member.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {
	USER("일반유저"),
	ADMIN("관리자 ");

	private final String description;

}

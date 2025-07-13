package com.example.green.domain.challengecert.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupRoleType {

	LEADER("팀 리더"),
	MEMBER("팀원");

	private final String description;
}

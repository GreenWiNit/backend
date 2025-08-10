package com.example.green.domain.challenge.entity.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupRoleType {

	LEADER("팀 리더"),
	MEMBER("팀원");

	private final String description;
}

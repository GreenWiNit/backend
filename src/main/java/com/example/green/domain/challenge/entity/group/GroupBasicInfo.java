package com.example.green.domain.challenge.entity.group;

import static com.example.green.global.utils.EntityValidator.*;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
public class GroupBasicInfo {
	@Column(length = 100, nullable = false)
	private String groupName;

	@Column(length = 500)
	private String description;

	@Column(length = 500)
	private String openChatUrl;

	private GroupBasicInfo(String groupName, String description, String openChatUrl) {
		validateEmptyString(groupName, "그룹 명은 비워있을 수 없습니다.");
		validateNullData(description, "그룹 설명이 NULL 입니다.");
		validateEmptyString(openChatUrl, "오픈 채팅 정보는 비워 있을 수 없습니다.");
		this.groupName = groupName;
		this.description = description;
		this.openChatUrl = openChatUrl;
	}

	public static GroupBasicInfo of(String groupName, String description, String openChatUrl) {
		return new GroupBasicInfo(groupName, description, openChatUrl);
	}
}
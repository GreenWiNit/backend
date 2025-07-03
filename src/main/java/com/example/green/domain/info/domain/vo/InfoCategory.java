package com.example.green.domain.info.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InfoCategory {
	EVENT("이벤트"),
	CONTENTS("콘텐츠"),
	ETC("기타");

	private final String description;
}

package com.example.green.domain.info.domain.vo;

import com.example.green.domain.info.exception.InfoException;
import com.example.green.domain.info.exception.InfoExceptionMessage;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InfoCategory {
	EVENT("이벤트"),
	CONTENTS("콘텐츠"),
	ETC("기타");

	private final String description;

	public String getCode() {
		return name();
	}

	// TODO [추후작업필요] Controller 단에서 검증
	@JsonCreator
	public static InfoCategory fromCode(String categoryCode) {
		for (InfoCategory category : InfoCategory.values()) {
			if (category.name().equalsIgnoreCase(categoryCode)) {
				return category;
			}
		}
		throw new InfoException(InfoExceptionMessage.INVALID_CATEGORY_CODE);
	}
}

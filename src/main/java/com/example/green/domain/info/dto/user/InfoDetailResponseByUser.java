package com.example.green.domain.info.dto.user;

import com.example.green.domain.info.domain.InfoEntity;

/**
 * 사용자 정보 상세
 * */
public record InfoDetailResponseByUser(
	String id,
	String infoCategoryName,
	String title

) {
	public static InfoDetailResponseByUser from(InfoEntity e) {
		return new InfoDetailResponseByUser(
			e.getId(),
			e.getInfoCategory().getDescription(),// @JsonValue 대신 명시적인 방법으로 사용
			e.getTitle()
		);
	}
}

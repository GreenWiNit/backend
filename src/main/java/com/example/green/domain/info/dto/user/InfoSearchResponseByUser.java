package com.example.green.domain.info.dto.user;

import com.example.green.domain.info.domain.InfoEntity;

/**
 * 사용자 정보 상세
 * */
public record InfoSearchResponseByUser(
	String id,
	String infoCategoryName,
	String title

) {
	public static InfoSearchResponseByUser from(InfoEntity e) {
		return new InfoSearchResponseByUser(
			e.getId(),
			e.getInfoCategory().getDescription(),// @JsonValue 대신 명시적인 방법으로 사용
			e.getTitle()
		);
	}
}

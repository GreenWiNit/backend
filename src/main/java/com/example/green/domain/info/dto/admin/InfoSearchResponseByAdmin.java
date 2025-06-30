package com.example.green.domain.info.dto.admin;

import java.time.LocalDateTime;

import com.example.green.domain.info.domain.InfoEntity;

/**
 * 관리자 정보 상세
 * */
public record InfoSearchResponseByAdmin(
	String id,
	String title,
	String infoCategoryName,
	String createdBy,
	String isDisplay,
	LocalDateTime createdDate // TODO [확인필요] Date 타입 프론트

) {
	public static InfoSearchResponseByAdmin from(InfoEntity e) {
		return new InfoSearchResponseByAdmin(
			e.getId(),
			e.getTitle(),
			e.getInfoCategory().getDescription(),// @JsonValue 대신 명시적인 방법으로 사용
			e.getCreatedBy(),
			e.getIsDisplay(),
			e.getCreatedDate()
		);
	}
}

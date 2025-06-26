package com.example.green.domain.info.dto.admin;

import java.time.LocalDateTime;

import com.example.green.domain.info.domain.InfoEntity;

/**
 * 관리자 정보 상세
 * */
public record AdminInfoSearchResponse(
	String id,
	String title,
	String infoCategoryName, // @JsonValue로 Json직렬화 단계 확인 가능 but 명시적인 방법 채택
	String registerId,
	String isDisplay,
	LocalDateTime createdDate // TODO [확인필요] Date 타입 프론트

) {
	public static AdminInfoSearchResponse from(InfoEntity e) {
		return new AdminInfoSearchResponse(
			e.getId(),
			e.getTitle(),
			e.getInfoCategory().getDescription(),
			String.valueOf(e.getRegisterId()),
			e.getIsDisplay(),
			e.getCreatedDate()
		);
	}
}

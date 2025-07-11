package com.example.green.domain.info.dto.user;

import com.example.green.domain.info.domain.InfoEntity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[사용자단] 정보 공유 목록 조회 단일 응답 DTO")
public record InfoSearchResponseByUser(
	@Schema(
		description = "정보 식별자 (ID)",
		example = "P000001"
	)
	String id,
	@Schema(
		description = "정보 카테고리 이름",
		allowableValues = {"이벤트", "컨텐츠", "기타"}
	)
	String infoCategoryName,

	@Schema(
		description = "정보 제목",
		example = "그린의 새로운 이벤트"
	)
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

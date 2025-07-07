package com.example.green.domain.info.dto.admin;

import java.time.LocalDateTime;

import com.example.green.domain.info.domain.InfoEntity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[관리자단] 정보 공유 목록 조회 단일 응답 DTO")
public record InfoSearchResponseByAdmin(
	@Schema(
		description = "정보 식별자 (ID)",
		example = "P000001"
	)
	String id,

	@Schema(
		description = "정보 제목",
		example = "그린의 새로운 이벤트"
	)
	String title,

	@Schema(
		description = "정보 카테고리 이름",
		allowableValues = {"이벤트", "컨텐츠", "기타"}
	)
	String infoCategoryName,

	@Schema(
		description = "정보 작성자 ID",
		example = "adminUser"
	)
	String createdBy,

	@Schema(
		description = "노출 여부",
		allowableValues = {"Y", "N"},
		example = "Y"
	)
	String isDisplay,

	@Schema(
		description = "정보 생성 일시 (ISO 8601)",
		example = "2023-10-01T12:00:00"
	)
	LocalDateTime createdDate // TODO [확인필요] JavaTimeModule이 문자열로 직렬화

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

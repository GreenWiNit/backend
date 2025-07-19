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
	String title,

	@Schema(
		description = "정보 본문 내용 (30자 이내)",
		example = "그린의 새로운 이벤트에 대해 자세히 알아보세요."
	)
	String content,

	@Schema(
		description = "첨부 이미지 URL (이미지 등록 후에 생성됨) 해당 경로를 통해 S3에서 이미지 반환",
		example = "https://static.greenwinit.store/images-image123.png"
	)
	String imageurl

) {
	public static InfoSearchResponseByUser from(InfoEntity e) {
		return new InfoSearchResponseByUser(
			e.getId(),
			e.getInfoCategory().getDescription(),// @JsonValue 대신 명시적인 방법으로 사용
			e.getTitle(),
			e.getContent(),
			e.getImageUrl()
		);
	}
}

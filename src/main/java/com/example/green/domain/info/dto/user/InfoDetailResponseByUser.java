package com.example.green.domain.info.dto.user;

import java.util.List;

import com.example.green.domain.info.domain.InfoEntity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[사용자단] 정보 공유 상세 조회 응답 DTO")
public record InfoDetailResponseByUser(
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
		description = "정보 본문 내용",
		example = "그린의 새로운 이벤트에 대해 자세히 알아보세요."
	)
	String content,

	@Schema(
		description = "첨부 이미지 URL 목록. 첫 번째 이미지가 썸네일로 사용됩니다.",
		example = "[\"https://static.greenwinit.store/image1.png\", \"https://static.greenwinit.store/image2.png\"]"
	)
	List<String> imageUrls

) {
	public static InfoDetailResponseByUser from(InfoEntity e) {
		return new InfoDetailResponseByUser(
			e.getId(),
			e.getTitle(),
			e.getInfoCategory().getDescription(), // @JsonValue 대신 명시적인 방법으로 사용
			e.getContent(),
			e.getImageUrls()
		);
	}
}

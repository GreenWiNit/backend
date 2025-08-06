package com.example.green.domain.info.dto.admin;

import java.time.LocalDateTime;

import com.example.green.domain.info.domain.InfoEntity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[관리자단] 정보 공유 상세 조회 응답 DTO")
public record InfoDetailResponseByAdmin(

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
		description = "정보 카테고리 코드",
		allowableValues = {"EVENT", "CONTENTS", "ETC"}
	)
	String infoCategoryCode,

	@Schema(
		description = "정보 본문 내용",
		example = "그린의 새로운 이벤트에 대해 자세히 알아보세요."
	)
	String content,

	@Schema(
		description = "첨부 이미지 URL (이미지 등록 후에 생성됨)",
		example = "https://static.greenwinit.store/images-image123.png"
	)
	String imageurl,

	@Schema(
		description = "마지막 수정자 id",
		example = "adminUser"
	)
	String lastModifiedBy,

	@Schema(
		description = "노출 여부",
		allowableValues = {"Y", "N"},
		example = "Y"
	)
	String isDisplay,

	@Schema(
		description = "마지막 수정 일시 (ISO 8601)",
		example = "2025-07-02T14:15:30"
	)
	LocalDateTime modifiedDate, // TODO [확인필요] Date 타입 프론트

	@Schema(
		description = "생성 일시 (ISO 8601)",
		example = "2025-06-25T09:00:00"
	)
	LocalDateTime createdDate

) {
	public static InfoDetailResponseByAdmin from(InfoEntity e) {
		return new InfoDetailResponseByAdmin(
			e.getId(),
			e.getTitle(),
			e.getInfoCategory().getDescription(),// 카테고리 한글명
			e.getInfoCategory().name(), // 카테고리 영문명 (수정용)
			e.getContent(),
			e.getImageUrl(),
			e.getLastModifiedBy(),
			e.getIsDisplay(),
			e.getModifiedDate(),
			e.getCreatedDate()
		);
	}
}

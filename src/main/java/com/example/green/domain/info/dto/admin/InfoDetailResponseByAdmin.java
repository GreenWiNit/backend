package com.example.green.domain.info.dto.admin;

import java.time.LocalDateTime;

import com.example.green.domain.info.domain.InfoEntity;

/**
 * 관리자 정보 상세
 * - 상세보기 페이지를 생성과 수정에서 함께 쓰고 있어서 Response 하나로 통함
 * */
public record InfoDetailResponseByAdmin(
	String id,
	String title,
	String infoCategoryName,
	String content,
	String imageurl,
	String lastModifiedBy,
	String isDisplay,
	LocalDateTime modifiedDate, // TODO [확인필요] Date 타입 프론트
	LocalDateTime createdDate

) {
	public static InfoDetailResponseByAdmin from(InfoEntity e) {
		return new InfoDetailResponseByAdmin(
			e.getId(),
			e.getTitle(),
			e.getInfoCategory().getDescription(),// @JsonValue 대신 명시적인 방법으로 사용
			e.getContent(),
			e.getImageUrl(),
			e.getLastModifiedBy(),
			e.getIsDisplay(),
			e.getModifiedDate(),
			e.getCreatedDate()
		);
	}
}

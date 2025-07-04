package com.example.green.domain.info.dto;

import com.example.green.domain.info.domain.InfoEntity;
import com.example.green.domain.info.domain.vo.InfoCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "정보 공유 요청(생성/수정) DTO")
public record InfoRequest(
	@Size(min = 1, max = 30, message = "제목은 최소 1자 이상, 최대 30자 이하까지 등록할 수 있습니다.")
	@Schema(type = "string",
		description = "정보 제목 (필수, 1자이상 30자 이하)")
	String title,

	@Size(min = 10, max = 1000, message = "내용은 최소 10자 이상, 최대 1000자 이하까지 등록할 수 있습니다.")
	@Schema(type = "string",
		description = "정보 내용 (필수, 10자 이상 1000자 이하)")
	String content,

	@Schema(type = "string",
		description = "정보 카테고리 (nullable)",
		allowableValues = {"EVENT", "CONTENTS", "ETC"})
	InfoCategory infoCategory,

	@NotBlank(message = "이미지가 첨부되지 않았습니다.")
	@Schema(type = "string",
		description = "정보 이미지 (필수, URL 형식) https://static.greenwinit.store/image-example.jpg")
	String imageUrl,

	@NotBlank(message = "전시여부를 선택해주세요.")
	@Schema(type = "string",
		description = "정보 전시여부 (필수)",
		allowableValues = {"Y", "N"})
	String isDisplay

) {

	public InfoEntity toEntity() {
		return InfoEntity.builder()
			.title(title)
			.content(content)
			.infoCategory(infoCategory)
			.imageUrl(imageUrl)
			.isDisplay(isDisplay)
			.build();
	}

}

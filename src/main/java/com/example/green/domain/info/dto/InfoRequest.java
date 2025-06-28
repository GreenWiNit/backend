package com.example.green.domain.info.dto;

import com.example.green.domain.info.domain.InfoEntity;
import com.example.green.domain.info.domain.vo.InfoCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * 관리자 정보 생성 및 수정
 * */

@Builder
public record InfoRequest(
	@NotBlank(message = "제목을 입력해주세요.")
	@Size(min = 1, max = 30, message = "제목은 최소 1자 이상, 최대 30자 이하까지 등록할 수 있습니다.")
	String title,
	@NotBlank(message = "내용을 입력해주세요.")
	@Size(min = 10, max = 1000, message = "내용은 최소 10자 이상, 최대 1000자 이하까지 등록할 수 있습니다.")
	String content,
	@NotBlank(message = "카테고리가 선택되지 않았습니다.")
	InfoCategory infoCategory,
	@NotBlank(message = "이미지가 첨부되지 않았습니다.")
	String imageUrl,
	@NotBlank(message = "전시여부를 선택해주세요.")
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

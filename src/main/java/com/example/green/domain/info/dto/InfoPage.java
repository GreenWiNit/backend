package com.example.green.domain.info.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "전체 페이지 조회 정보")
public record InfoPage(
	@Schema(
		description = "전체 게시글 수",
		example = "124"
	)
	long totalElements,

	@Schema(
		description = "전체 페이지 수",
		example = "13"
	)
	int totalPages
) {
}

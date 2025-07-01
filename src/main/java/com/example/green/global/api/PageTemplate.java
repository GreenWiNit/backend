package com.example.green.global.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(description = "페이지 요청")
public record PageTemplate<T>(
	@Schema(description = "전체 데이터 수", example = "10000")
	long totalElements,
	@Schema(description = "총 페이지 수", example = "100")
	int totalPages,
	@Schema(description = "현재 페이지 수", example = "10")
	int currentPage,
	@Schema(description = "페이지 당 데이터 수", example = "10")
	int pageSize,
	@Schema(description = "다음 페이지 여부")
	boolean hasNext,
	@Schema(description = "데이터")
	@NonNull
	List<T> content
) {
}

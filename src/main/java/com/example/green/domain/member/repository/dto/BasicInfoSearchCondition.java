package com.example.green.domain.member.repository.dto;

import com.example.green.global.api.page.PageSearchCondition;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "멤버 포인트 검색 조건")
public record BasicInfoSearchCondition(
	@Schema(description = "검색 키워드(nullable)")
	String keyword,
	@Schema(description = "페이지 수 (nullable)")
	Integer page,
	@Schema(description = "페이지 당 사이즈 수 (nullable)")
	Integer size
) implements PageSearchCondition {
}

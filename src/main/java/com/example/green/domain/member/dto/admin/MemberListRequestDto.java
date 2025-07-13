package com.example.green.domain.member.dto.admin;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Schema(description = "관리자용 회원 목록 조회 요청")
public record MemberListRequestDto(
	@Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
	@Min(value = 0, message = "페이지는 0 이상이어야 합니다.")
	Integer page,
	
	@Schema(description = "페이지 크기", example = "10", defaultValue = "10")
	@Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
	@Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
	Integer size
) {
	public MemberListRequestDto {
		if (page == null) page = 0;
		if (size == null) size = 10;
	}
	
	public Pageable toPageable() {
		return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
	}
} 
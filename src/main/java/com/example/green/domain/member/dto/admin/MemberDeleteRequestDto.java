package com.example.green.domain.member.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "관리자용 회원 삭제 요청")
public record MemberDeleteRequestDto(
	@Schema(description = "삭제할 회원의 회원키 (고유 식별자)", example = "naver 123456789", required = true)
	@NotBlank(message = "회원키는 필수입니다.")
	String memberKey
) {
} 
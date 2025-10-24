package com.example.green.domain.dashboard.rankingmodule.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 상위 회원 정보 응답")
public record TopMemberPointResponseDto(

	@Schema(description = "닉네임", example = "홍길동")
	String nickname,
	@Schema(description = "총 적립 포인트", example = "10000")
	BigDecimal totalEarned,
	@Schema(description = "챌린지 인증 개수", example = "37")
	int verificationCount
) {
}

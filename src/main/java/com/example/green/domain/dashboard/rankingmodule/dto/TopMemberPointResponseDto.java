package com.example.green.domain.dashboard.rankingmodule.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "포인트 상위 회원 정보 응답")
public record TopMemberPointResponseDto(

	@NotNull
	@Schema(description = "회원 ID", example = "1")
	Long memberId,

	@NotNull
	@Schema(description = "닉네임", example = "홍길동")
	String nickname,

	@NotNull
	@PositiveOrZero
	@Schema(description = "총 포인트", example = "123")
	BigDecimal totalEarned,

	@NotNull
	@PositiveOrZero
	@Schema(description = "챌린지 인증 개수", example = "37")
	int verificationCount,

	@NotNull
	@Schema(description = "주 시작일", example = "2025-10-20")
	LocalDate weekStart,

	@NotNull
	@Schema(description = "주 종료일", example = "2025-10-26")
	LocalDate weekEnd
	
) {
}

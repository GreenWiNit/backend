package com.example.green.domain.dashboard.rankingmodule.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "회원의 주간 데이터")
public record MemberPointResponse(
	@NotNull
	@Schema(description = "회원 ID", example = "1")
	Long memberId,

	@NotNull
	@Schema(description = "닉네임", example = "홍길동")
	String nickname,

	@NotNull
	@Schema(description = "프로필 사진", example = "https://my-app-profile.s3.ap-northeast-2.amazonaws.com/profile/user123.png")
	String profileImageUrl,

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

package com.example.green.domain.member.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 목록 조회 응답")
public record UserSummaryDto(

	@Schema(description = "회원 ID", example = "1")
	Long userId,

	@Schema(description = "닉네임", example = "환경지킴이")
	String nickname,

	@Schema(description = "프로필 이미지 URL", nullable = true)
	String profileImageUrl,

	@Schema(description = "자기 자신 여부", example = "false")
	boolean isMe,

	@Schema(description = "총 챌린지 인증 횟수", example = "42")
	long totalCertificationCount,

	@Schema(description = "현재 보유 포인트", example = "1500.00")
	BigDecimal currentPoints

) {

	public static UserSummaryDto of(
		Long userId,
		String nickname,
		String profileImageUrl,
		Long currentMemberId,
		long certCount,
		BigDecimal points
	) {
		boolean isMe = currentMemberId != null && currentMemberId.equals(userId);
		return new UserSummaryDto(
			userId,
			nickname,
			profileImageUrl,
			isMe,
			certCount,
			points != null ? points : BigDecimal.ZERO
		);
	}
}
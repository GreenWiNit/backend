package com.example.green.domain.mypage.dto;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.bind.DefaultValue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마이페이지 메인 응답 DTO")
public record MypageMainResponseDto(

	@DefaultValue("0")
	@Schema(type = "int",
		description = "회원 챌린지 참여 총 횟수")
	int userChallengeCount,

	@DefaultValue("0")
	@Schema(type = "BigDecimal",
		description = "회원 총 보유 포인트")
	BigDecimal userTotalPoints,

	@DefaultValue("1")
	@Schema(type = "int",
		description = "회원 레벨(현 포인트로 계산된 값)")
	int userLevel
) {
}

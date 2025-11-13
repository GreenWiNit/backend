package com.example.green.domain.dashboard.growth.dto.response;

import java.math.BigDecimal;

import com.example.green.domain.dashboard.growth.entity.enums.Level;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자의 식물 성장 데이터")
public record LoadGrowthResponse(

	@Schema(description = "사용자 ID", example = "1")
	Long memberId,

	@Schema(description = "사용자 목표 레벨", example = "새싹")
	Level goalLevel,

	@Schema(description = "사용자 현재 레벨", example = "흙")
	Level currentLevel,

	@Schema(description = "다음 레벨까지 남은 퍼센트", example = "45")
	BigDecimal nextLevelPercent,

	@Schema(description = "다음 레벨까지 도달하기 위한 포인트", example = "250")
	BigDecimal nextLevelPoint
) {
}


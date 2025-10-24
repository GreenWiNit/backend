package com.example.green.domain.dashboard.rankingmodule.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주간 포인트 랭킹 응답")
public record WeeklyRankingResponse(
	@Schema(description = "상위 회원 리스트")
	List<TopMemberPointResponseDto> topMembers,

	@Schema(description = "본인 정보")
	TopMemberPointResponseDto myData
) {
}

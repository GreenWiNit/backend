package com.example.green.domain.dashboard.rankingmodule.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeeklyRankingResponseMessage implements ResponseMessage {
	LOAD_WEEKLY_RANKING_SUCCESS("주간 환경 챌린저 랭킹 조회에 성공했습니다");

	private final String message;
}

package com.example.green.domain.mypage.dto;

/**
 * 마이페이지 메인 응답 DTO
 * */
public record MypageMainResponseDto(
	// TODO [확인필요] @임현정 @김지환 반환 값이 int를 주어도 되는지 확인
	int userChallengeCount,
	int userTotalPoints
) {
}

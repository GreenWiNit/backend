package com.example.green.domain.mypage.client;

public interface ChallengeCountGetClient {
	/**
	 * 사용자별 챌린자 참여 횟수
	 *
	 * @param userId 사용자 ID
	 * @return 챌린지 카운트
	 */
	int getChallengeCount(Long userId);
}

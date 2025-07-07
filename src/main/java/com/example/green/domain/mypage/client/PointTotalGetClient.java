package com.example.green.domain.mypage.client;

public interface PointTotalGetClient {

	/**
	 * 사용자별 총 포인트 조회
	 *
	 * @param userId 사용자 ID
	 * @return 포인트 정보
	 */
	int getTotalPoints(Long userId);
}

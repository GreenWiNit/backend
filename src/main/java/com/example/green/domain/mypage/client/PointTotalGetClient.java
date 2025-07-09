package com.example.green.domain.mypage.client;

import java.math.BigDecimal;

public interface PointTotalGetClient {

	/**
	 * 사용자별 총 포인트 조회
	 *
	 * @param userId 사용자 ID
	 * @return 포인트 정보
	 */
	BigDecimal getTotalPoints(Long userId);
}

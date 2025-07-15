package com.example.green.domain.mypage.service;

import java.math.BigDecimal;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.example.green.domain.mypage.client.PointTotalGetClient;
import com.example.green.domain.mypage.dto.MypageMainResponseDto;
import com.example.green.domain.mypage.exception.MypageException;
import com.example.green.domain.mypage.exception.MypageExceptionMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 마이페이지 관련 비즈니스 로직을 처리하는 서비스 클래스
 * - Facade + BFF (api 없는) 패턴 적용하여 결합도 낮춤
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class MypageFacadeService {

	// 포인트에 따른 레벨 매핑
	private static final NavigableMap<BigDecimal, Integer> POINT_TO_LEVEL;

	static {
		POINT_TO_LEVEL = new TreeMap<>();
		POINT_TO_LEVEL.put(BigDecimal.ZERO, 1);  // 0 이상 → 1LV
		POINT_TO_LEVEL.put(new BigDecimal("1000"), 2);  // 1000 이상 → 2LV
		POINT_TO_LEVEL.put(new BigDecimal("2000"), 3);  // 2000 이상 → 3LV
		POINT_TO_LEVEL.put(new BigDecimal("4000"), 4);  // 4000 이상 → 4LV
		POINT_TO_LEVEL.put(new BigDecimal("10000"), 10); // 10000 이상 → 10LV
	}

	// TODO [확인필요] @임현정 챌린지 서비스 단에서 implement 하여 회원별 챌린지 카운트 조회 구현
	//private final ChallengeCountGetClient challengeCountGetClient;
	private final PointTotalGetClient pointTotalGetClient;

	public MypageMainResponseDto getMypageMain(Long memberId) {
		// int userChallengeCount = challengeCountGetClient.getChallengeCount(memberId);
		BigDecimal userTotalPoints = pointTotalGetClient.getTotalPoints(memberId);
		int userLevel = getUserLevel(userTotalPoints);
		return new MypageMainResponseDto(0, userTotalPoints, userLevel);
	}

	private int getUserLevel(BigDecimal userTotalPoints) {
		if (userTotalPoints == null) {
			log.info("마이페이지 메인 조회시 포인트가 null로 들어오는 오류.");
			throw new MypageException(MypageExceptionMessage.NULL_USER_TOTAL_POINTS);
		}
		return POINT_TO_LEVEL.floorEntry(userTotalPoints)
			.getValue();
	}
}

package com.example.green.domain.mypage.service;

import org.springframework.stereotype.Service;

import com.example.green.domain.mypage.dto.MypageMainResponseDto;

import lombok.RequiredArgsConstructor;

// TODO [확인필요] 마이페이지단 예시 샘플 : 회원조회 기준값 확인 현재 memberNo로 잡음

/**
 * 마이페이지 관련 비즈니스 로직을 처리하는 서비스 클래스
 * - Facade + BFF (api 없는) 패턴 적용하여 결합도 낮춤
 * */
@Service
@RequiredArgsConstructor
public class MypageFacadeService {
	// TODO [확인필요] @임현정 챌린지 서비스 단에서 implement 하여 회원별 챌린지 카운트 조회 구현
	//private final ChallengeCountGetClient challengeCountGetClient;
	// TODO [확인필요] @김지환 포인트 PointTransactionAdaptor 단에서 implement 하여 회원별 챌린지 카운트 조회 구현
	//private final PointTotalGetClient pointTotalGetClient;

	public MypageMainResponseDto getMypageMain(Long memberNo) {
		// int userChallengeCount = challengeCountGetClient.getChallengeCount(memberNo); // 예시로 memberNo 1L 사용
		// int userTotalPoints = pointTotalGetClient.getTotalPoints(memberNo); // 예시로 memberNo 1L 사용
		return new MypageMainResponseDto(0, 0); // 임시 반환
	}
}

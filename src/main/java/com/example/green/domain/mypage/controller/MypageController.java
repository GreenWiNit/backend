package com.example.green.domain.mypage.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.mypage.controller.api.MypageResponseMessage;
import com.example.green.domain.mypage.dto.MypageMainResponseDto;
import com.example.green.domain.mypage.service.MypageFacadeService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

// TODO [확인필요] 마이페이지단 예시 샘플 : 회원조회 기준값 확인 현재 memberNo로 잡음

/**
 * 마이페이지 관련 프리젠테이션 레이어 컨트롤러
 * - 마이페이지 메인 카운트만 담당
 * - 상세 클릭시 각각 모듈 API로 처리
 * 	(예: 챌린지 클릭 > /api/user/challenge/{userId})
 * 	(예: 회원 클릭 > /api/user/member/{userId})
 * */

@RestController
@RequiredArgsConstructor
public class MypageController {
	private final MypageFacadeService mypageFacadeService;

	// 마이페이지 메인 조회 API
	@GetMapping("/api/user/mypage")
	public ApiTemplate<MypageMainResponseDto> getMypageMain(@AuthenticationPrincipal PrincipalDetails principal) {
		Long memberNo = principal.getMemberId(); // TODO [확인 필요] @김지환 @임현정 변경 필요
		MypageMainResponseDto mypageMainDto = mypageFacadeService.getMypageMain(memberNo);
		return ApiTemplate.ok(MypageResponseMessage.GET_MYPAGE_MAIN_SUCCESS, mypageMainDto);
	}
}

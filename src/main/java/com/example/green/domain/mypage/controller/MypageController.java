package com.example.green.domain.mypage.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.mypage.controller.api.MypageResponseMessage;
import com.example.green.domain.mypage.dto.MypageMainResponseDto;
import com.example.green.domain.mypage.service.MypageFacadeService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import lombok.RequiredArgsConstructor;

/**
 * 마이페이지 관련 프리젠테이션 레이어 컨트롤러
 * - 마이페이지 메인 카운트만 담당
 * - 상세 클릭시 각각 모듈 API로 처리
 * */

@RestController
@RequiredArgsConstructor
public class MypageController implements MypageControllerDocs {
	private final MypageFacadeService mypageFacadeService;

	// 마이페이지 메인 조회 API
	@AuthenticatedApi(reason = "로그인한 사용자만 마이페이지 조회 가능")
	@GetMapping("/api/user/mypage")
	public ApiTemplate<MypageMainResponseDto> getMypageMain(@AuthenticationPrincipal PrincipalDetails principal) {
		Long memberId = principal.getMemberId();
		MypageMainResponseDto mypageMainDto = mypageFacadeService.getMypageMain(memberId);
		return ApiTemplate.ok(MypageResponseMessage.GET_MYPAGE_MAIN_SUCCESS, mypageMainDto);
	}
}

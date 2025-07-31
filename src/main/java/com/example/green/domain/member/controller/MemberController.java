package com.example.green.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.green.domain.member.controller.docs.MemberControllerDocs;
import com.example.green.domain.member.controller.message.MemberResponseMessage;
import com.example.green.domain.member.dto.NicknameCheckRequestDto;
import com.example.green.domain.member.dto.NicknameCheckResponseDto;
import com.example.green.domain.member.dto.ProfileUpdateRequestDto;
import com.example.green.domain.member.dto.ProfileUpdateResponseDto;
import com.example.green.domain.member.dto.WithdrawRequestDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.service.MemberService;
import com.example.green.domain.member.service.WithdrawService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;
import com.example.green.global.security.annotation.PublicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {

	private final MemberService memberService;
	private final WithdrawService withdrawService;

	@Override
	@AuthenticatedApi(reason = "본인의 프로필 수정은 로그인이 필요합니다")
	@PutMapping("/profile")
	public ApiTemplate<ProfileUpdateResponseDto> updateProfile(
		@Valid @RequestBody ProfileUpdateRequestDto request,
		@AuthenticationPrincipal PrincipalDetails currentUser) {

		Long memberId = currentUser.getMemberId();

		Member updatedMember = memberService.updateProfile(
			memberId,
			request.getNickname(),
			request.getProfileImageUrl()
		);

		ProfileUpdateResponseDto response = ProfileUpdateResponseDto.from(updatedMember);

		log.info("[MEMBER] 프로필 업데이트 성공: memberId={} nickname={}", 
			memberId, request.getNickname());

		return ApiTemplate.ok(MemberResponseMessage.PROFILE_UPDATED, response);
	}

	@Override
	@PublicApi
	@PostMapping("/nickname-check")
	public ResponseEntity<NicknameCheckResponseDto> checkNickname(@Valid @RequestBody NicknameCheckRequestDto request) {
		log.info("[NICKNAME_CHECK] 닉네임 중복 확인 요청: {}", request.nickname());

		boolean isAvailable = memberService.isNicknameAvailable(request.nickname());

		String message;

		if (isAvailable) {
			message = MemberResponseMessage.NICKNAME_AVAILABLE.getMessage();
		} else {
			message = MemberResponseMessage.NICKNAME_TAKEN.getMessage();
		}

		NicknameCheckResponseDto response = new NicknameCheckResponseDto(
			request.nickname(),
			isAvailable,
			message
		);

		log.info("[NICKNAME_CHECK] 닉네임 중복 확인 완료: {} - {}", request.nickname(), message);
		return ResponseEntity.ok(response);
	}

	@Override
	@AuthenticatedApi(reason = "회원 탈퇴는 로그인한 사용자만 가능합니다")
	@PostMapping("/withdraw")
	public ResponseEntity<Void> withdraw(
		@AuthenticationPrincipal PrincipalDetails currentUser,
		@Valid @RequestBody WithdrawRequestDto withdrawRequest) {
		
		String memberKey = currentUser.getUsername();

		log.info("[WITHDRAW] 회원 탈퇴 요청 - memberKey: {}, reasonType: {}", 
				 memberKey, withdrawRequest.reasonType());

		withdrawService.withdrawMemberWithReason(memberKey, withdrawRequest);

		log.info("[WITHDRAW] 회원 탈퇴 완료 - memberKey: {}, reasonType: {}", 
				 memberKey, withdrawRequest.reasonType());
		return ResponseEntity.ok().build();
	}
}

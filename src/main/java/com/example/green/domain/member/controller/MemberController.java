package com.example.green.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.green.domain.member.controller.docs.MemberControllerDocs;
import com.example.green.domain.member.controller.message.MemberResponseMessage;
import com.example.green.domain.member.dto.MemberInfoResponseDto;
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

	@AuthenticatedApi(reason = "자신의 정보를 조회할 수 있습니다")
	@GetMapping("/me")
	public ApiTemplate<MemberInfoResponseDto> getCurrentMemberInfo(
		@AuthenticationPrincipal PrincipalDetails currentUser) {
		
		Long memberId = currentUser.getMemberId();
		Member member = memberService.getCurrentMemberInfo(memberId);
		MemberInfoResponseDto response = MemberInfoResponseDto.from(member);
		
		log.info("[MEMBER] 현재 사용자 정보 조회: memberId={}, nickname={}", 
			memberId, member.getProfile().getNickname());
		
		return ApiTemplate.ok(MemberResponseMessage.MEMBER_INFO_RETRIEVED, response);
	}

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
	@Deprecated
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

	@PublicApi
	@PostMapping("/v2/nickname-check")
	public ApiTemplate<NicknameCheckResponseDto> checkNicknameV2(@Valid @RequestBody NicknameCheckRequestDto request) {
		log.info("[NICKNAME_CHECK_V2] 닉네임 중복 확인 요청: {}", request.nickname());

		boolean isAvailable = memberService.isNicknameAvailable(request.nickname());

		String message;
		MemberResponseMessage responseMessage;

		if (isAvailable) {
			message = MemberResponseMessage.NICKNAME_AVAILABLE.getMessage();
			responseMessage = MemberResponseMessage.NICKNAME_AVAILABLE;
		} else {
			message = MemberResponseMessage.NICKNAME_TAKEN.getMessage();
			responseMessage = MemberResponseMessage.NICKNAME_TAKEN;
		}

		NicknameCheckResponseDto response = new NicknameCheckResponseDto(
			request.nickname(),
			isAvailable,
			message
		);

		log.info("[NICKNAME_CHECK_V2] 닉네임 중복 확인 완료: {} - {}", request.nickname(), message);
		return ApiTemplate.ok(responseMessage, response);
	}

	@Override
	@AuthenticatedApi(reason = "회원 탈퇴는 로그인한 사용자만 가능합니다")
	@PostMapping("/withdraw")
	@Deprecated
	public ResponseEntity<Void> withdraw(
		@AuthenticationPrincipal PrincipalDetails currentUser,
		@Valid @RequestBody WithdrawRequestDto withdrawRequest) {
		
		String memberKey = currentUser.getUsername();

		log.info("[WITHDRAW] 회원 탈퇴 요청 - memberKey: {}, reasonTypes: {}", 
				 memberKey, withdrawRequest.reasonTypes());

		withdrawService.withdrawMemberWithReason(memberKey, withdrawRequest);

		log.info("[WITHDRAW] 회원 탈퇴 완료 - memberKey: {}, reasonTypes: {}", 
				 memberKey, withdrawRequest.reasonTypes());
		return ResponseEntity.ok().build();
	}

	@AuthenticatedApi(reason = "회원 탈퇴는 로그인한 사용자만 가능합니다")
	@PostMapping("/v2/withdraw")
	public ApiTemplate<Void> withdrawV2(
		@AuthenticationPrincipal PrincipalDetails currentUser,
		@Valid @RequestBody WithdrawRequestDto withdrawRequest) {
		
		String memberKey = currentUser.getUsername();

		log.info("[WITHDRAW_V2] 회원 탈퇴 요청 - memberKey: {}, reasonTypes: {}", 
				 memberKey, withdrawRequest.reasonTypes());

		withdrawService.withdrawMemberWithReason(memberKey, withdrawRequest);

		log.info("[WITHDRAW_V2] 회원 탈퇴 완료 - memberKey: {}, reasonTypes: {}", 
				 memberKey, withdrawRequest.reasonTypes());
		return ApiTemplate.ok(MemberResponseMessage.MEMBER_WITHDRAWN);
	}
}

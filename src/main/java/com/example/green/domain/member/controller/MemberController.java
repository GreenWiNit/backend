package com.example.green.domain.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.member.controller.docs.MemberControllerDocs;
import com.example.green.domain.member.dto.PhoneInfoResponseDto;
import com.example.green.domain.member.dto.PhoneInfoResultDto;
import com.example.green.domain.member.dto.ProfileUpdateRequestDto;
import com.example.green.domain.member.dto.ProfileUpdateResponseDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.service.MemberService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {

	private final MemberService memberService;

	@AuthenticatedApi(reason = "본인의 프로필 수정은 로그인이 필요합니다")
	@PutMapping("/profile")
	@Override
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

		return ApiTemplate.ok(() -> "프로필이 성공적으로 수정되었습니다.", response);
	}

	@AuthenticatedApi(reason = "본인의 휴대폰 정보 조회는 로그인이 필요합니다")
	@GetMapping("/phone")
	@Override
	public ApiTemplate<PhoneInfoResponseDto> getPhoneInfo(
		@AuthenticationPrincipal PrincipalDetails currentUser) {

		Long memberId = currentUser.getMemberId();

		PhoneInfoResultDto result = memberService.getPhoneInfo(memberId);
		PhoneInfoResponseDto response = PhoneInfoResponseDto.of(result.getMember(), result.isAuthenticated());

		log.info("[MEMBER] 휴대폰 정보 조회 성공: memberId={} phoneNumber={} isAuthenticated={}", 
			memberId, response.phoneNumber(), response.isAuthenticated());

		return ApiTemplate.ok(() -> "휴대폰 정보를 성공적으로 조회했습니다.", response);
	}
} 
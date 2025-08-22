package com.example.green.domain.member.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.green.domain.member.controller.message.MemberResponseMessage;
import com.example.green.domain.member.dto.NicknameCheckRequestDto;
import com.example.green.domain.member.dto.NicknameCheckResponseDto;
import com.example.green.domain.member.service.MemberService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.annotation.PublicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/members")
@RequiredArgsConstructor
public class MemberV2Controller {

	private final MemberService memberService;

	@PublicApi
	@PostMapping("/nickname-check")
	public ApiTemplate<NicknameCheckResponseDto> checkNickname(@Valid @RequestBody NicknameCheckRequestDto request) {
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
}
package com.example.green.domain.example.tools;

import static com.example.green.domain.auth.constants.AuthConstants.*;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.auth.admin.entity.Admin;
import com.example.green.domain.auth.dto.TokenResponseDto;
import com.example.green.domain.auth.entity.vo.AccessToken;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.auth.utils.WebUtils;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Hidden
public class FakeAuthTools {

	private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7일

	private final MemberRepository memberRepository;
	private final TokenService tokenService;

	@GetMapping("/auth/{id}")
	public ResponseEntity<TokenResponseDto> fakeAuth(@PathVariable Long id,
		HttpServletRequest httpRequest,
		HttpServletResponse response) {
		Optional<Member> byId = memberRepository.findById(id);
		Member member = byId.get();
		String memberKey = member.getMemberKey();
		String accessTokenString = tokenService.createAccessToken(memberKey, ROLE_USER);
		AccessToken accessToken = AccessToken.from(accessTokenString, tokenService);
		String refreshTokenString = tokenService.createRefreshToken(
			memberKey,
			WebUtils.extractDeviceInfo(httpRequest),
			WebUtils.extractClientIp(httpRequest)
		);
		Cookie cookie = WebUtils.createRefreshTokenCookie(
			refreshTokenString,
			WebUtils.isSecureRequest(httpRequest),
			REFRESH_TOKEN_MAX_AGE
		);
		response.addCookie(cookie);

		return ResponseEntity.ok(new TokenResponseDto(
			accessToken.getValue(),
			member.getMemberKey(),
			member.getName()
		));
	}

	@GetMapping("/auth/admin")
	public ResponseEntity<TokenResponseDto> fakeAuth() {
		Optional<Member> byId = memberRepository.findById(1L);
		Member member = byId.get();
		String accessTokenString = tokenService.createAccessToken(member.getMemberKey(), Admin.ROLE_ADMIN);
		AccessToken accessToken = AccessToken.from(accessTokenString, tokenService);

		return ResponseEntity.ok(new TokenResponseDto(
			accessToken.getValue(),
			member.getMemberKey(),
			member.getName()
		));
	}
}

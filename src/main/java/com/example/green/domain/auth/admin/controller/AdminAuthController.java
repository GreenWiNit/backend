package com.example.green.domain.auth.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.auth.admin.dto.AdminLoginRequestDto;
import com.example.green.domain.auth.admin.dto.AdminLoginResponseDto;
import com.example.green.domain.auth.admin.entity.Admin;
import com.example.green.domain.auth.admin.service.AdminService;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.global.security.annotation.PublicApi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Auth API", description = "어드민 인증 관련 API")
public class AdminAuthController {

	private final AdminService adminService;
	private final TokenService tokenService;

	@PublicApi(reason = "어드민 로그인은 인증 없이 접근 가능해야 합니다")
	@Operation(summary = "어드민 로그인", description = "어드민 계정으로 로그인하여 JWT 토큰을 발급받습니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그인 성공",
			content = @Content(schema = @Schema(implementation = AdminLoginResponseDto.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(examples = @ExampleObject(value = "{\"message\":\"비밀번호가 일치하지 않습니다.\"}"))),
		@ApiResponse(responseCode = "404", description = "계정을 찾을 수 없음",
			content = @Content(examples = @ExampleObject(value = "{\"message\":\"해당 관리자를 찾을 수 없습니다.\"}")))
	})
	@PostMapping("/login")
	public ResponseEntity<AdminLoginResponseDto> login(@Valid @RequestBody AdminLoginRequestDto request) {

		Admin admin = adminService.authenticate(request.getLoginId(), request.getPassword());

		String accessToken = tokenService.createAccessToken(admin.getTokenMemberKey(), Admin.ROLE_ADMIN);

		AdminLoginResponseDto response = AdminLoginResponseDto.of(accessToken, admin);

		log.info("[ADMIN_AUTH] 로그인 성공: {} ({})", admin.getLoginId(), admin.getName());
		return ResponseEntity.ok(response);
	}
} 
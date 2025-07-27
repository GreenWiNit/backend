package com.example.green.domain.member.controller.docs;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.green.domain.member.dto.PhoneInfoResponseDto;
import com.example.green.domain.member.dto.ProfileUpdateRequestDto;
import com.example.green.domain.member.dto.ProfileUpdateResponseDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Member API", description = "회원 프로필 관리 API")
public interface MemberControllerDocs {

	@Operation(
		summary = "프로필 수정",
		description = """
			사용자의 닉네임과 프로필 이미지를 수정합니다.
			프로필 이미지는 먼저 /api/images 엔드포인트로 업로드한 후 받은 URL을 사용합니다.
			"""
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "프로필 수정 성공",
			content = @Content(schema = @Schema(implementation = ProfileUpdateResponseDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
			content = @Content(examples = @ExampleObject(value = "{\"message\":\"닉네임은 2자 이상 20자 이하로 입력해주세요.\"}"))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(examples = @ExampleObject(value = "{\"message\":\"로그인이 필요합니다.\"}"))),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
			content = @Content(examples = @ExampleObject(value = "{\"message\":\"해당 회원을 찾을 수 없습니다.\"}"))),
		@ApiResponse(responseCode = "500", description = "프로필 업데이트 실패",
			content = @Content(examples = @ExampleObject(value = "{\"message\":\"프로필 업데이트에 실패했습니다.\"}")))
	})
	ApiTemplate<ProfileUpdateResponseDto> updateProfile(
		@Parameter(description = "프로필 업데이트 요청 데이터", required = true)
		@Valid @RequestBody ProfileUpdateRequestDto request,
		@AuthenticationPrincipal PrincipalDetails currentUser
	);

	@Operation(
		summary = "휴대폰 정보 조회",
		description = """
			사용자의 휴대폰 번호와 인증 상태를 조회합니다.
			포인트상점 이용 시 휴대폰 인증이 필요하므로 인증 상태도 함께 반환합니다.
			"""
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "휴대폰 정보 조회 성공",
			content = @Content(schema = @Schema(implementation = PhoneInfoResponseDto.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(examples = @ExampleObject(value = "{\"message\":\"로그인이 필요합니다.\"}"))),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
			content = @Content(examples = @ExampleObject(value = "{\"message\":\"해당 회원을 찾을 수 없습니다.\"}"))),
		@ApiResponse(responseCode = "500", description = "휴대폰 정보 조회 실패",
			content = @Content(examples = @ExampleObject(value = "{\"message\":\"휴대폰 정보 조회에 실패했습니다.\"}")))
	})
	ApiTemplate<PhoneInfoResponseDto> getPhoneInfo(
		@AuthenticationPrincipal PrincipalDetails currentUser
	);
} 
package com.example.green.domain.mypage.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.green.domain.mypage.dto.MypageMainResponseDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.DetailedExceptionResponse;
import com.example.green.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "마이페이지 API", description = "마이페이지 메인 API")
public interface MypageControllerDocs {

	@Operation(summary = "사용자 마이페이지 메인 조회", description = "회원별 포인트, 레벨, 챌린지 참여횟수를 조회합니다.")
	@ApiErrorStandard
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "관리자 정보공유 목록 조회 성공", useReturnTypeSchema = true),
		@ApiResponse(
			responseCode = "400",
			description = "사용자의 총 포인트는 NULL 일 수 없습니다",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = DetailedExceptionResponse.class),
				examples = @ExampleObject(
					name = "NotNullForUserTotalPoints",
					summary = "마이페이지 레벨 계산 시 포인트가 NULL인 경우",
					value = """
						{
						  "success": false,
						  "message": "사용자의 총 포인트는 NULL 일 수 없습니다",
						}
						"""
				)
			)
		),
	})
	ApiTemplate<MypageMainResponseDto> getMypageMain(@AuthenticationPrincipal PrincipalDetails principal);

}

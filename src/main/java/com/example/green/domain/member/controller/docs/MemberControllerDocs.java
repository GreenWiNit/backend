package com.example.green.domain.member.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.green.domain.member.dto.NicknameCheckRequestDto;
import com.example.green.domain.member.dto.NicknameCheckResponseDto;
import com.example.green.domain.member.dto.ProfileUpdateRequestDto;
import com.example.green.domain.member.dto.ProfileUpdateResponseDto;
import com.example.green.domain.member.dto.WithdrawRequestDto;
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
        @Valid ProfileUpdateRequestDto request,
        @AuthenticationPrincipal PrincipalDetails currentUser);

    @Operation(
        summary = "닉네임 중복 확인",
        description = "입력된 닉네임이 이미 사용 중인지 확인합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "닉네임 중복 확인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NicknameCheckResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                        "nickname": "홍길동",
                        "available": true,
                        "message": "사용 가능한 닉네임입니다."
                    }
                    """)
            )),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (닉네임 누락 등)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "error": "INVALID_REQUEST",
                        "message": "닉네임을 입력해주세요."
                    }
                    """)
            ))
    })
    ResponseEntity<NicknameCheckResponseDto> checkNickname(@Valid NicknameCheckRequestDto request);

    @Operation(
        summary = "회원 탈퇴",
        description = "현재 로그인한 사용자의 회원 탈퇴를 처리합니다. " +
            "탈퇴 사유와 함께 계정을 비활성화합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "탈퇴 요청 정보 (탈퇴 사유 포함)",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = WithdrawRequestDto.class),
                examples = @ExampleObject(value = """
                    {
                        "reasonType": "SERVICE_DISSATISFACTION",
                        "customReason": "챌린지 기능이 부족해서 탈퇴합니다."
                    }
                    """)
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "회원 탈퇴 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "회원 탈퇴가 완료되었습니다."
                    }
                    """)
            )),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (이미 탈퇴한 회원, 필수 정보 누락 등)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "이미 탈퇴한 회원입니다."
                    }
                    """)
            ))
    })
    ResponseEntity<Void> withdraw(
        @AuthenticationPrincipal PrincipalDetails currentUser,
        @Valid WithdrawRequestDto withdrawRequest);
} 
package com.example.green.domain.auth.controller.docs;

import com.example.green.domain.auth.dto.SignupRequestDto;
import com.example.green.domain.auth.dto.TokenResponseDto;
import com.example.green.global.api.ApiTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "인증 V2 API", description = "OAuth2 로그인, 회원가입 V2 API (공통 응답 형식)")
public interface AuthV2ControllerDocs {

    @Operation(
        summary = "회원가입",
        description = "OAuth2 로그인 후 신규 사용자의 회원가입을 처리합니다. 공통 응답 형식을 사용합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "회원가입 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiTemplate.class),
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "회원가입이 성공적으로 완료되었습니다.",
                        "result": {
                            "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "memberKey": "google_123456789",
                            "userName": "홍길동"
                        }
                    }
                    """))),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (임시 토큰 만료, 필수 필드 누락 등)",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "임시 토큰 만료",
                        value = """
                            {
                                "success": false,
                                "message": "임시 토큰이 만료되었습니다."
                            }
                            """),
                    @ExampleObject(
                        name = "닉네임 필수값 누락",
                        value = """
                            {
                                "success": false,
                                "message": "유효하지 않은 요청입니다.",
                                "errors": [
                                    {
                                        "fieldName": "nickname",
                                        "message": "닉네임은 필수입니다."
                                    }
                                ]
                            }
                            """),
                    @ExampleObject(
                        name = "탈퇴한 사용자 재가입 차단",
                        value = """
                            {
                                "success": false,
                                "message": "탈퇴한 사용자는 재가입할 수 없습니다."
                            }
                            """)
                }))
    })
    ApiTemplate<TokenResponseDto> signup(
        @Parameter(
            description = "회원가입 요청 정보 (임시 토큰, 닉네임, 프로필 이미지 URL)",
            required = true,
            content = @Content(
                schema = @Schema(implementation = SignupRequestDto.class),
                examples = @ExampleObject(value = """
                    {
                        "tempToken": "eyJhbGciOiJIUzI1NiJ9...",
                        "nickname": "홍길동",
                        "profileImageUrl": "https://example.com/profile.jpg"
                    }
                    """)
            )
        )
        SignupRequestDto request,
        HttpServletRequest httpRequest,
        HttpServletResponse response
    );
}
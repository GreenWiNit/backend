package com.example.green.domain.auth.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.green.domain.auth.dto.SignupRequestDto;
import com.example.green.domain.auth.dto.TokenResponseDto;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = " [인증-클라이언트] 멤버(회원)인증 API", description = "OAuth2 로그인, 회원가입, 토큰 관리 등 인증 관련 API")
public interface AuthControllerDocs {

    @Operation(
        summary = "회원가입",
        description = "OAuth2 로그인 후 신규 사용자의 회원가입을 처리합니다. " + 
                     "임시 토큰에서 Google 계정 정보를 추출하고, 추가 정보를 받아 회원 등록을 완료합니다.",
        deprecated = true
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "회원가입 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenResponseDto.class),
                examples = @ExampleObject(value = """
                    {
                    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                    "memberKey": "google_123456789",
                    "userName": "홍길동"}
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
                        name = "닉네임 공백 포함",
                        value = """
                            {
                                "success": false,
                                "message": "닉네임에 공백을 포함할 수 없습니다."
                            }
                            """),
                    @ExampleObject(
                        name = "닉네임 특수문자 포함",
                        value = """
                            {
                                "success": false,
                                "message": "닉네임은 한글, 영문, 숫자만 사용 가능합니다."
                            }
                            """),
                    @ExampleObject(
                        name = "닉네임 길이 제한",
                        value = """
                            {
                                "success": false,
                                "message": "닉네임은 2자 이상 20자 이하여야 합니다."
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
    ResponseEntity<TokenResponseDto> signup(
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

    @Operation(
        summary = "AccessToken 갱신",
        description = "TokenManager(쿠키)을 사용하여 만료된 AccessToken을 " + 
                     "새로 발급받습니다. RefreshToken은 HTTP-Only 쿠키로 자동 전송됩니다.",
        deprecated = true
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "AccessToken 갱신 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenResponseDto.class),
                examples = @ExampleObject(
                    value = """
                        {
                        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                        "memberKey": "google_123456789",
                        "userName": null
                        }
                        """
                )
            )),
        @ApiResponse(
            responseCode = "400",
            description = "RefreshToken이 없거나 유효하지 않음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                        "error": "INVALID_REFRESH_TOKEN",
                        "message": "RefreshToken이 없거나 만료되었습니다."
                        }
                        """
                )
            )),
        @ApiResponse(
            responseCode = "401",
            description = "TokenManager 만료 또는 무효화됨",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                        "error": "TOKEN_EXPIRED",
                        "message": "다시 로그인해주세요."
                        }
                        """
                )
            ))
    })
    ResponseEntity<TokenResponseDto> refreshToken(HttpServletRequest request);

    @Operation(
        summary = "로그아웃",
        description = "현재 디바이스에서 로그아웃합니다. RefreshToken을 DB에서 무효화합니다.",
        deprecated = true
    )
    ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response,
        @AuthenticationPrincipal PrincipalDetails currentUser);

    @Operation(
        summary = "모든 디바이스 로그아웃",
        description = "해당 사용자의 모든 디바이스에서 로그아웃합니다. 모든 토큰을 무효화합니다.",
        deprecated = true
    )
    ResponseEntity<Void> logoutAll(HttpServletRequest request, HttpServletResponse response,
        @AuthenticationPrincipal PrincipalDetails currentUser);

    @Operation(
        summary = "회원가입 V2",
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
                        name = "닉네임 공백 포함",
                        value = """
                            {
                                "success": false,
                                "message": "닉네임에 공백을 포함할 수 없습니다."
                            }
                            """),
                    @ExampleObject(
                        name = "닉네임 특수문자 포함",
                        value = """
                            {
                                "success": false,
                                "message": "닉네임은 한글, 영문, 숫자만 사용 가능합니다."
                            }
                            """),
                    @ExampleObject(
                        name = "닉네임 길이 제한",
                        value = """
                            {
                                "success": false,
                                "message": "닉네임은 2자 이상 20자 이하여야 합니다."
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
    ApiTemplate<TokenResponseDto> signupV2(
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

    @Operation(
        summary = "AccessToken 갱신 V2",
        description = """
            RefreshToken(쿠키)을 사용하여 만료된 AccessToken을 새로 발급받습니다.
            RefreshToken은 HTTP-Only 쿠키로 자동 전송됩니다.
            공통 응답 형식을 사용합니다.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "AccessToken 갱신 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiTemplate.class),
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "토큰이 성공적으로 갱신되었습니다.",
                        "result": {
                            "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "memberKey": "google_123456789",
                            "userName": null
                        }
                    }
                    """))),
        @ApiResponse(
            responseCode = "400",
            description = "RefreshToken이 없거나 유효하지 않음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "RefreshToken이 없거나 만료되었습니다."
                    }
                    """)))
    })
    ApiTemplate<TokenResponseDto> refreshTokenV2(HttpServletRequest request);

    @Operation(
        summary = "로그아웃 V2",
        description = """
            현재 디바이스에서 로그아웃합니다.
            RefreshToken을 DB에서 무효화하고 쿠키를 삭제합니다.
            공통 응답 형식을 사용합니다.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiTemplate.class),
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "로그아웃이 완료되었습니다.",
                        "result": null
                    }
                    """))),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "인증이 필요합니다."
                    }
                    """)))
    })
    ApiTemplate<Void> logoutV2(
        HttpServletRequest request,
        HttpServletResponse response,
        @AuthenticationPrincipal PrincipalDetails currentUser
    );

    @Operation(
        summary = "모든 디바이스 로그아웃 V2",
        description = """
            해당 사용자의 모든 디바이스에서 로그아웃합니다.
            모든 RefreshToken을 무효화하고 현재 디바이스의 쿠키를 삭제합니다.
            공통 응답 형식을 사용합니다.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "모든 디바이스 로그아웃 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiTemplate.class),
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "모든 디바이스에서 로그아웃이 완료되었습니다.",
                        "result": null
                    }
                    """))),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "인증이 필요합니다."
                    }
                    """)))
    })
    ApiTemplate<Void> logoutAllV2(
        HttpServletRequest request,
        HttpServletResponse response,
        @AuthenticationPrincipal PrincipalDetails currentUser
    );
} 
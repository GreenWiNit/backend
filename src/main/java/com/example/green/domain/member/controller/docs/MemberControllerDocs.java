package com.example.green.domain.member.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.green.domain.member.dto.MemberInfoResponseDto;
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
        summary = "현재 사용자 정보 조회",
        description = """
            현재 로그인한 사용자의 기본 정보를 조회합니다.
            
            ## 조회 정보
            - 닉네임
            - 이메일 주소
            - 프로필 이미지 URL (있는 경우)
            
            ## 인증 요구사항
            - 유효한 JWT 토큰이 필요합니다
            - 탈퇴한 회원은 조회할 수 없습니다
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "회원 정보 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiTemplate.class),
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "회원 정보 조회에 성공했습니다.",
                        "data": {
                            "nickname": "환경지킴이",
                            "email": "user@example.com",
                            "profileImageUrl": "https://example.com/profile.jpg"
                        }
                    }
                    """)
            )),
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
                    """)
            )),
        @ApiResponse(
            responseCode = "404", 
            description = "사용자를 찾을 수 없음 (탈퇴한 회원 등)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "해당 회원을 찾을 수 없습니다."
                    }
                    """)
            ))
    })
    ApiTemplate<MemberInfoResponseDto> getCurrentMemberInfo(
        @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails currentUser);

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
        description = """
            현재 로그인한 사용자의 회원 탈퇴를 처리합니다.
            
            ## 탈퇴 사유 목록 (reasonType)
            
            | 코드 | 설명 | customReason |
            |------|------|--------------|
            | `SERVICE_DISSATISFACTION` | 서비스 이용이 불편해요 | 무시됨 |
            | `POLICY_DISAGREEMENT` | 원하는 정보가 없어요 | 무시됨 |
            | `PRIVACY_CONCERN` | 다른 서비스를 이용할 예정이에요 | 무시됨 |
            | `PRIVACY_PROTECTION` | 개인정보 보호를 위해 탈퇴할게요 | 무시됨 |
            | `OTHER` | 기타 | **필수 입력** |
            
            ## 주의사항
            - `reasonType`이 `OTHER`인 경우 `customReason` 필드 필수 입력
            - `OTHER` 외의 타입 선택 시 `customReason` 값은 무시됨 (null 처리)
            - `customReason`은 최대 1000자까지 입력 가능
            - 탈퇴 처리 후 모든 토큰이 무효화됩니다
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "탈퇴 요청 정보 (탈퇴 사유 포함)",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = WithdrawRequestDto.class),
                examples = {
                    @ExampleObject(
                        name = "일반 사유로 탈퇴",
                        summary = "미리 정의된 사유 (customReason 무시됨)",
                        value = """
                            {
                                "reasonType": "SERVICE_DISSATISFACTION",
                                "customReason": null
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "기타 사유로 탈퇴",
                        summary = "OTHER 선택 시 customReason 필수",
                        value = """
                            {
                                "reasonType": "OTHER",
                                "customReason": "원하는 기능이 없어서 탈퇴합니다."
                            }
                            """
                    )
                }
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
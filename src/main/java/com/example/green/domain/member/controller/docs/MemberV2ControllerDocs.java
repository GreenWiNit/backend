package com.example.green.domain.member.controller.docs;

import com.example.green.domain.member.dto.NicknameCheckRequestDto;
import com.example.green.domain.member.dto.NicknameCheckResponseDto;
import com.example.green.global.api.ApiTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Member V2 API", description = "회원 관리 V2 API (공통 응답 형식)")
public interface MemberV2ControllerDocs {

    @Operation(
        summary = "닉네임 중복 확인",
        description = "입력된 닉네임이 이미 사용 중인지 확인합니다. (공통 응답 형식 사용)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "닉네임 중복 확인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiTemplate.class),
                examples = {
                    @ExampleObject(
                        name = "사용 가능한 닉네임",
                        value = """
                            {
                                "success": true,
                                "message": "사용 가능한 닉네임입니다.",
                                "result": {
                                    "nickname": "홍길동",
                                    "available": true,
                                    "message": "사용 가능한 닉네임입니다."
                                }
                            }
                            """),
                    @ExampleObject(
                        name = "중복된 닉네임",
                        value = """
                            {
                                "success": true,
                                "message": "중복된 닉네임이 존재합니다.",
                                "result": {
                                    "nickname": "홍길동",
                                    "available": false,
                                    "message": "중복된 닉네임이 존재합니다."
                                }
                            }
                            """)
                }
            )),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (닉네임 누락 등)",
            content = @Content(
                mediaType = "application/json",
                examples = {
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
                        name = "닉네임 길이 제한 초과",
                        value = """
                            {
                                "success": false,
                                "message": "유효하지 않은 요청입니다.",
                                "errors": [
                                    {
                                        "fieldName": "nickname",
                                        "message": "닉네임은 2자 이상 20자 이하로 입력해주세요."
                                    }
                                ]
                            }
                            """),
                    @ExampleObject(
                        name = "닉네임 형식 오류",
                        value = """
                            {
                                "success": false,
                                "message": "유효하지 않은 요청입니다.",
                                "errors": [
                                    {
                                        "fieldName": "nickname",
                                        "message": "닉네임은 한글, 영문, 숫자만 사용 가능합니다."
                                    }
                                ]
                            }
                            """)
                }
            ))
    })
    ApiTemplate<NicknameCheckResponseDto> checkNickname(@Valid NicknameCheckRequestDto request);
}
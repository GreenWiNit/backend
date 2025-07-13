package com.example.green.domain.member.controller.docs;

import com.example.green.domain.member.repository.dto.BasicInfoSearchCondition;
import com.example.green.domain.member.repository.dto.MemberPointsDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "사용자 관련 API", description = "사용자 관련 API 모음 입니다.")
public interface MemberAdminControllerDocs {

	@Operation(summary = "사용자 포인트 페이지 조회 (관리자)", description = "사용자 포인트 페이지를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "상사용자 포인트 페이지 조회 성공.")
	ApiTemplate<PageTemplate<MemberPointsDto>> searchMembersPoint(BasicInfoSearchCondition condition);
}
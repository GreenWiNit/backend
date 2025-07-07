package com.example.green.domain.point.controller.docs;

import com.example.green.domain.point.controller.dto.PointTransactionSearchCondition;
import com.example.green.domain.point.repository.dto.PointTransactionDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "포인트 관련 API", description = "포인트 관련 API 모음 입니다.")
public interface PointTransactionAdminControllerDocs {

	@Operation(summary = "사용자 포인트 조회 (관리자)", description = "사용자 포인트를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 내역 조회에 성공했습니다.")
	ApiTemplate<PageTemplate<PointTransactionDto>> getPointsByMember(
		Long memberId,
		PointTransactionSearchCondition condition
	);
}

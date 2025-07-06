package com.example.green.domain.point.controller.docs;

import com.example.green.domain.point.controller.dto.MemberPointSummary;
import com.example.green.global.api.ApiTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "포인트 관련 API", description = "포인트 관련 API 모음 입니다.")
public interface PointTransactionControllerDocs {

	@Operation(summary = "내 포인트 내역 조회", description = "현재 포인트 내역을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 내역 조회에 성공했습니다.")
	ApiTemplate<MemberPointSummary> getPointSummary();
}

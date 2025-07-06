package com.example.green.domain.point.controller.docs;

import com.example.green.domain.point.controller.dto.MemberPointSummary;
import com.example.green.domain.point.controller.dto.MyPointTransaction;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "포인트 관련 API", description = "포인트 관련 API 모음 입니다.")
public interface PointTransactionControllerDocs {

	@Operation(summary = "내 포인트 조회", description = "현재 포인트를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "내 포인트 조회에 성공했습니다.")
	ApiTemplate<MemberPointSummary> getPointSummary();

	@Operation(summary = "내 포인트 내역 조회", description = "현재 포인트 내역을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 내역 조회에 성공했습니다.")
	ApiTemplate<CursorTemplate<Long, MyPointTransaction>> getMyPointTransaction(
		@Schema(description = "커서 정보", example = "1") Long cursor,
		@Schema(description = "포인트 내역 상태", type = "string", allowableValues = {"earn", "spend"}) TransactionType status
	);
}

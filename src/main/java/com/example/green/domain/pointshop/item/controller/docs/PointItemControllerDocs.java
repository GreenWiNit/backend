package com.example.green.domain.pointshop.item.controller.docs;

import com.example.green.domain.pointshop.item.dto.response.PointItemClientResponse;
import com.example.green.domain.pointshop.item.dto.response.PointItemResponse;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.error.dto.ExceptionResponse;
import com.example.green.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "포인트 상점 아이템 API", description = "포인트 상점 아이템 관련 API 모음 입니다.")
public interface PointItemControllerDocs {

	@Operation(summary = "포인트 상점 아이템 목록 조회", description = "커서 기반 목록 조회입니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상점 아이템 목록 조회에 성공했습니다.")
	ApiTemplate<CursorTemplate<Long, PointItemResponse>> getItems(Long cursor);

	@Operation(summary = "포인트 상점 아이템 상세 조회", description = "아이템 상세 조회입니다")
	@ApiResponse(responseCode = "200", description = "아이템 상세 조회에 성공했습니다")
	@ApiResponse(
		responseCode = "404", description = "아이템을 찾을 수 없습니다",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<PointItemClientResponse> getPointItem(PrincipalDetails principal, Long itemId);
}

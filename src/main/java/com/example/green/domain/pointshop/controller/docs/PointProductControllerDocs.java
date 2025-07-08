package com.example.green.domain.pointshop.controller.docs;

import com.example.green.domain.pointshop.controller.dto.PointProductDetail;
import com.example.green.domain.pointshop.repository.dto.PointProductView;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "포인트 상품 API", description = "포인트 상품 관련 API 모음 입니다.")
public interface PointProductControllerDocs {

	@Operation(summary = "포인트 상품 목록 조회", description = "커서 기반 무한 스크롤 목록 조회입니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상품 목록 조회에 성공했습니다.")
	ApiTemplate<CursorTemplate<Long, PointProductView>> getProducts(Long cursor);

	@Operation(summary = "포인트 상품 상세 조회", description = "단일 상품 상세 조회입니다.")
	@ApiResponse(responseCode = "200", description = "단일 상품 조회에 성공했습니다.")
	@ApiResponse(
		responseCode = "404", description = "포인트 상품을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<PointProductDetail> getProductById(Long pointProductId);
}

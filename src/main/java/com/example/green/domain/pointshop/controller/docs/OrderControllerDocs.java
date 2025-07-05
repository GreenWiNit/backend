package com.example.green.domain.pointshop.controller.docs;

import com.example.green.domain.pointshop.controller.dto.SingleOrderRequest;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "주문(상품 교환) API", description = "주문 관련 API 모음 입니다.")
public interface OrderControllerDocs {

	@Operation(summary = "포인트 상품 단일 교환", description = "단일 포인트 상품을 교환합니다.")
	@ApiResponse(responseCode = "200", description = "배송지 정보 추가에 성공했습니다.")
	@ApiResponse(
		responseCode = "400", description = """
		1. 상품 수량은 최소 1개부터 최대 5개까지 선택할 수 있습니다.
		2. 상품 재고가 부족합니다.
		3. 사용 가능한 포인트가 부족합니다.
		""",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiResponse(
		responseCode = "401", description = "해당 배송지의 소유자가 아닙니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiResponse(
		responseCode = "404", description = """
		1. 포인트 상품을 찾을 수 없습니다.
		2. 배송지 정보를 찾을 수 없습니다.
		""",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<Long> exchangeSinglePointProduct(SingleOrderRequest dto);
}

package com.example.green.domain.pointshop.order.controller.docs;

import com.example.green.domain.pointshop.order.controller.dto.ExchangeApplicationResult;
import com.example.green.domain.pointshop.order.controller.dto.ExchangeApplicationSearchCondition;
import com.example.green.domain.pointshop.order.controller.dto.PointProductApplicantResult;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "주문(상품 교환) API", description = "주문 관련 API 모음 입니다.")
public interface OrderAdminControllerDocs {

	@Operation(summary = "교환 신청자 목록 조회 (관리자)", description = "포인트 상품 교환 신청자 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "상품 교환 신청자 목록 조회에 성공했습니다.")
	ApiTemplate<PageTemplate<PointProductApplicantResult>> getExchangeApplicant(
		@Parameter(in = ParameterIn.PATH, description = "포인트 상품 식별자", required = true, example = "1")
		Long pointProductId,
		@Parameter(in = ParameterIn.QUERY, description = "현재 페이지 (nullable)", example = "1")
		Integer page,
		@Parameter(in = ParameterIn.QUERY, description = "페이지 당 사이즈 (nullable", example = "10")
		Integer size
	);

	@Operation(summary = "교환 신청 목록 조회 (관리자)", description = "포인트 상품 교환 신청 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "상품 교환 신쳥 목록 조회에 성공했습니다.")
	ApiTemplate<PageTemplate<ExchangeApplicationResult>> searchExchangeApplication(
		ExchangeApplicationSearchCondition condition);

	@Operation(summary = "상품 배송 상태로 변경 (관리자)", description = "주문된 상품 교환 내역을 배송중인 상태로 변경합니다.")
	@ApiResponse(responseCode = "200", description = "주문이 배송 시작 상태로 변경되었습니다.")
	@ApiResponse(responseCode = "400", description = "배송 대기 상태가 아닙니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	@ApiResponse(responseCode = "404", description = "주문 정보를 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	NoContent shipOrder(@Parameter(description = "주문 식별자", example = "1") Long orderId);

	@Operation(summary = "상품 배송 상태로 변경 (관리자)", description = "배송중인 상품 교환 내역을 배송 완료 상태로 변경합니다.")
	@ApiResponse(responseCode = "200", description = "주문이 배송 완료 상태로 변경되었습니다.")
	@ApiResponse(responseCode = "400", description = "배송 상태가 아닙니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	@ApiResponse(responseCode = "404", description = "주문 정보를 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	NoContent completeDelivery(@Parameter(description = "주문 식별자", example = "1") Long orderId);
}

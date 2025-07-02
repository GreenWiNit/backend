package com.example.green.domain.pointshop.controller.docs;

import com.example.green.domain.pointshop.controller.dto.PointProductCreateDto;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchResponse;
import com.example.green.domain.pointshop.controller.dto.PointProductUpdateDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.docs.ApiError400;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "포인트 상품 API", description = "포인트 상품 관련 API 모음 입니다.")
public interface PointProductControllerDocs {

	@Operation(summary = "포인트 상품 생성 (관리자)", description = "포인트 상품을 생성합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상품 생성에 성공했습니다.")
	@ApiError400
	@ApiResponse(
		responseCode = "400", description = "포인트 상품 생성 시 잘못된 정보 기입하면 발생",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<Long> createPointProduct(PointProductCreateDto dto);

	@Operation(summary = "포인트 상품 목록 조회(관리자)", description = "포인트 상품 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상품 목록 조회에 성공했습니다.")
	ApiTemplate<PageTemplate<PointProductSearchResponse>> findPointProducts(PointProductSearchCondition condition);

	@Operation(summary = "포인트 상품 수정", description = "포인트 상품을 수정합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상품 수정에 성공했습니다.")
	@ApiResponse(
		responseCode = "400", description = "중복된 상품 코드가 존재합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiResponse(
		responseCode = "404", description = "포인트 상품을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent updatePointProduct(PointProductUpdateDto dto, Long pointProductId);
}

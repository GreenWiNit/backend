package com.example.green.domain.pointshop.product.controller.docs;

import com.example.green.domain.pointshop.product.controller.dto.PointProductCreateDto;
import com.example.green.domain.pointshop.product.controller.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchResult;
import com.example.green.domain.pointshop.product.controller.dto.PointProductUpdateDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "포인트 상품 API", description = "포인트 상품 관련 API 모음 입니다.")
public interface PointProductAdminControllerDocs {

	@Operation(summary = "포인트 상품 생성 (관리자)", description = "포인트 상품을 생성합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상품 생성에 성공했습니다.")
	@ApiResponse(
		responseCode = "400", description = "포인트 상품 생성 시 잘못된 정보 기입하면 발생",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<Long> createPointProduct(PointProductCreateDto dto);

	@Operation(summary = "포인트 상품 목록 조회(관리자)", description = "포인트 상품 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상품 목록 조회에 성공했습니다.")
	ApiTemplate<PageTemplate<PointProductSearchResult>> findPointProducts(PointProductSearchCondition condition);

	@Operation(summary = "포인트 상품 목록 엑셀 다운로드(관리자)", description = "포인트 상품 목록 다운로드")
	@ApiResponse(responseCode = "200", description = "첨부파일")
	@ApiResponse(
		responseCode = "500", description = """
		1. 엑셀 파일 생성에 실패했습니다.
		2. 엑셀로 추출하려는 데이터가 비어있습니다.
		""",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	void findPointProducts(PointProductExcelCondition condition, HttpServletResponse response);

	@Operation(summary = "포인트 상품 수정(관리자)", description = "포인트 상품을 수정합니다.")
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

	@Operation(summary = "포인트 상품 삭제(관리자)", description = "포인트 상품을 삭제합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상품 삭제에 성공했습니다.")
	@ApiResponse(
		responseCode = "404", description = "포인트 상품을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent deletePointProduct(Long pointProductId);

	@Operation(summary = "포인트 상품 전시(관리자)", description = "포인트 상품을 전시합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상품이 전시 상태가 됐습니다.")
	@ApiResponse(
		responseCode = "404", description = "포인트 상품을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent showDisplay(Long pointProductId);

	@Operation(summary = "포인트 상품 미전시(관리자)", description = "포인트 상품을 미전시 처리합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상품이 미전시 상태가 됐습니다.")
	@ApiResponse(
		responseCode = "404", description = "포인트 상품을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent hideDisplay(Long pointProductId);
}

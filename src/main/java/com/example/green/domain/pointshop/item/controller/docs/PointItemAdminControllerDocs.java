package com.example.green.domain.pointshop.item.controller.docs;

import com.example.green.domain.pointshop.item.dto.request.PointItemExcelDownloadRequest;
import com.example.green.domain.pointshop.item.dto.request.PointItemSearchRequest;
import com.example.green.domain.pointshop.item.dto.response.ItemWithApplicantsDTO;
import com.example.green.domain.pointshop.item.dto.response.PointItemAdminResponse;
import com.example.green.domain.pointshop.item.dto.response.PointItemSearchResponse;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "포인트 상점 아이템 API(관리자)", description = "포인트 상점 아이템 관련 관리자용 API 문서입니다.")
public interface PointItemAdminControllerDocs {

	@Operation(summary = "포인트 상점 아이템 목록 조회(관리자)", description = "조건에 맞는 포인트 아이템 목록을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "아이템 목록 조회에 성공했습니다.")
	ApiTemplate<PageTemplate<PointItemSearchResponse>> findPointItems(PointItemSearchRequest request);

	@Operation(summary = "포인트 상점 아이템 상세 조회(관리자)", description = "아이템 상세 정보를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "상세 조회 성공")
	@ApiResponse(
		responseCode = "404", description = "아이템을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<PointItemAdminResponse> showPointItem(Long pointItemId);

	@Operation(summary = "포인트 상점 아이템 전시(관리자)", description = "아이템을 전시 상태로 변경합니다.")
	@ApiResponse(responseCode = "200", description = "아이템이 전시 상태가 됐습니다")
	@ApiResponse(
		responseCode = "404", description = "아이템을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent showPointItemDisplay(Long pointItemId);

	@Operation(summary = "포인트 상점 아이템 미전시(관리자)", description = "아이템을 미전시 상태로 변경합니다.")
	@ApiResponse(responseCode = "200", description = "아이템이 미전시 상태가 됐습니다")
	@ApiResponse(
		responseCode = "404", description = "아이템을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent hidePointItemDisplay(Long pointItemId);

	@Operation(summary = "포인트 아이템 엑셀 다운로드(관리자)", description = "검색 조건에 맞는 포인트 아이템 목록을 엑셀로 다운로드합니다.")
	@ApiResponse(responseCode = "200", description = "엑셀 파일 다운로드 성공")
	@ApiResponse(
		responseCode = "500", description = "엑셀 생성 실패 혹은 다운로드 대상 데이터 없음",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	void downloadPointItemsExcel(PointItemExcelDownloadRequest request, HttpServletResponse response);

	@Operation(
		summary = "포인트 아이템 주문 내역 조회(관리자)",
		description = "모든 포인트 아이템 주문 내역을 페이지네이션하여 조회합니다. ")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "포인트 아이템 주문 내역 조회 성공",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ItemWithApplicantsDTO.class))
		),
		@ApiResponse(responseCode = "500", description = "주문 내역 조회 중 서버 오류 발생",
			content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
		)
	})
	ApiTemplate<PageTemplate<ItemWithApplicantsDTO>> findAllOrders(Integer page, Integer size);

}

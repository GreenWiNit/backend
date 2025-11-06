package com.example.green.domain.pointshop.item.controller.docs;

import com.example.green.domain.pointshop.item.dto.request.CreatePointItemRequest;
import com.example.green.domain.pointshop.item.dto.request.PointItemExcelDownloadRequest;
import com.example.green.domain.pointshop.item.dto.request.PointItemSearchRequest;
import com.example.green.domain.pointshop.item.dto.request.UpdatePointItemRequest;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "포인트 상점 아이템 API(관리자)", description = "포인트 상점 아이템 관련 관리자용 API 문서입니다.")
public interface PointItemAdminControllerDocs {

	@Operation(summary = "포인트 상점 아이템 생성(관리자)", description = "포인트 상점 아이템을 생성합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상점 아이템 생성에 성공했습니다")
	@ApiResponse(
		responseCode = "400", description = "잘못된 요청입니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<Long> createPointItem(CreatePointItemRequest request);

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

	@Operation(summary = "포인트 상점 아이템 수정(관리자)", description = "포인트 상점 아이템 정보를 수정합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상점 아이템 수정에 성공했습니다")
	@ApiResponse(
		responseCode = "400", description = "중복된 코드 또는 잘못된 데이터",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiResponse(
		responseCode = "404", description = "아이템을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent updatePointItem(UpdatePointItemRequest request, Long pointItemId);

	@Operation(summary = "포인트 상점 아이템 삭제(관리자)", description = "포인트 상점 아이템을 삭제합니다.")
	@ApiResponse(responseCode = "200", description = "아이템 삭제에 성공했습니다")
	@ApiResponse(
		responseCode = "404", description = "아이템을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent deletePointItem(Long pointItemId);

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

}

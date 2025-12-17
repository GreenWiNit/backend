package com.example.green.domain.pointshop.admin.controller.docs;

import com.example.green.domain.pointshop.admin.dto.request.AdminCreatePointShopRequest;
import com.example.green.domain.pointshop.admin.dto.request.AdminUpdatePointShopRequest;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "포인트 상점 상품 / 아이템 어드민 API", description = "포인트 상점 상품 / 아이템 어드민 API 모음 입니다.")
public interface PointShopAdminControllerDocs {

	@Operation(summary = "포인트 아이템 / 상품 생성 (관리자)", description = "포인트 아이템 / 상품 을 생성합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 아이템 / 상품 생성에 성공했습니다.")
	@ApiResponse(
		responseCode = "400", description = "포인트 아이템 / 상품 생성 시 잘못된 정보 기입하면 발생",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<Long> create(AdminCreatePointShopRequest createPointShopRequest);

	@Operation(summary = "포인트 상점 아이템 / 상품 수정(관리자)", description = "포인트 상점 아이템 / 상품 정보를 수정합니다.")
	@ApiResponse(responseCode = "200", description = "포인트 상점 아이템 / 상품 수정에 성공했습니다")
	@ApiResponse(
		responseCode = "400", description = "중복된 코드 또는 잘못된 데이터",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiResponse(
		responseCode = "404", description = "아이템 / 상품 을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent update(Long id, AdminUpdatePointShopRequest updatePointShopRequest);

	@Operation(summary = "포인트 상점 아이템 / 상품 삭제(관리자)", description = "포인트 상점 아이템 / 상품 을 삭제합니다.")
	@ApiResponse(responseCode = "200", description = "아이템 / 상품 삭제에 성공했습니다")
	@ApiResponse(
		responseCode = "404", description = "아이템 / 상품 을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent delete(Long id);
}

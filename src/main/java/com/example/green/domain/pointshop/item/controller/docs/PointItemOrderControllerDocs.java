package com.example.green.domain.pointshop.item.controller.docs;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.green.domain.pointshop.item.dto.request.OrderPointItemRequest;
import com.example.green.domain.pointshop.item.dto.response.OrderPointItemResponse;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.error.dto.ExceptionResponse;
import com.example.green.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "포인트 상점 아이템 교환 API", description = "포인트 상점 아이템 교환 관련 API 모음입니다")
public interface PointItemOrderControllerDocs {

	@Operation(
		summary = "포인트 아이템 주문",
		description = "로그인한 사용자가 지정한 포인트 아이템을 구매합니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "아이템 구매 성공",
				content = @Content(schema = @Schema(implementation = OrderPointItemResponse.class))),
			@ApiResponse(responseCode = "404", description = "해당 아이템을 찾을 수 없음",
				content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
			@ApiResponse(responseCode = "400", description = "포인트 부족 또는 잘못된 요청",
				content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
		}
	)
	@PostMapping("/{itemId}")
	ApiTemplate<OrderPointItemResponse> orderPointItem(
		@Parameter(description = "주문할 포인트 아이템의 ID", example = "1")
		@PathVariable Long itemId,

		@Parameter(description = "구매 수량 및 기타 정보")
		@RequestBody @Valid OrderPointItemRequest orderPointItemRequest,

		@AuthenticationPrincipal PrincipalDetails principal
	);
}

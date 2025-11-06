package com.example.green.domain.pointshop.item.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.item.controller.docs.PointItemControllerDocs;
import com.example.green.domain.pointshop.item.controller.message.PointItemResponseMessage;
import com.example.green.domain.pointshop.item.dto.response.PointItemClientResponse;
import com.example.green.domain.pointshop.item.dto.response.PointItemResponse;
import com.example.green.domain.pointshop.item.repository.PointItemQueryRepository;
import com.example.green.domain.pointshop.item.service.PointItemService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.PublicApi;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/point-items")
@RequiredArgsConstructor
public class PointItemController implements PointItemControllerDocs {

	private final PointItemService pointItemService;
	private final PointItemQueryRepository pointItemQueryRepository;

	@GetMapping
	@PublicApi
	public ApiTemplate<CursorTemplate<Long, PointItemResponse>> getItems(
		@RequestParam(required = false) Long cursor
	) {
		CursorTemplate<Long, PointItemResponse> items = pointItemQueryRepository.getPointItemsByCursor(cursor);
		return ApiTemplate.ok(PointItemResponseMessage.POINT_ITEMS_INQUIRY_SUCCESS, items);
	}

	@GetMapping("/{itemId}")
	@PublicApi
	public ApiTemplate<PointItemClientResponse> getPointItem(
		@AuthenticationPrincipal(errorOnInvalidType = false) PrincipalDetails principal,
		@PathVariable Long itemId
	) {
		Long memberId = (principal != null) ? principal.getMemberId() : null;

		PointItemClientResponse response = pointItemService.getPointItemInfo(memberId, itemId);

		return ApiTemplate.ok(PointItemResponseMessage.POINT_ITEM_DETAIL_INQUIRY_SUCCESS, response);
	}

}

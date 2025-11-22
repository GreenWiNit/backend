package com.example.green.domain.pointshop.item.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.item.controller.docs.PointItemOrderControllerDocs;
import com.example.green.domain.pointshop.item.controller.message.PointItemResponseMessage;
import com.example.green.domain.pointshop.item.dto.request.OrderPointItemRequest;
import com.example.green.domain.pointshop.item.dto.response.OrderPointItemResponse;
import com.example.green.domain.pointshop.item.entity.PointItem;
import com.example.green.domain.pointshop.item.entity.vo.PointItemSnapshot;
import com.example.green.domain.pointshop.item.exception.PointItemException;
import com.example.green.domain.pointshop.item.exception.PointItemExceptionMessage;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;
import com.example.green.domain.pointshop.item.service.PointItemOrderService;
import com.example.green.domain.pointshop.item.service.command.OrderPointItemCommand;
import com.example.green.domain.pointshop.order.entity.vo.MemberSnapshot;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/point-items/order")
@RequiredArgsConstructor
public class PointItemOrderController implements PointItemOrderControllerDocs {

	private final PointItemOrderService pointItemOrderService;
	private final PointItemRepository pointItemRepository;

	//@Idempotent
	@AuthenticatedApi(reason = "로그인한 사용자만 아이템을 구매 가능하다")
	@PostMapping("/{itemId}")
	public ApiTemplate<OrderPointItemResponse> orderPointItem(
		@PathVariable Long itemId,
		@Valid @RequestBody OrderPointItemRequest orderPointItemRequest,
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		PointItem item = pointItemRepository.findById(itemId)
			.orElseThrow(() -> new PointItemException(PointItemExceptionMessage.NOT_FOUND_ITEM));

		MemberSnapshot memberSnapshot = new MemberSnapshot(principal.getMemberId(), principal.getMemberKey(),
			principal.getEmail());

		PointItemSnapshot itemSnapShot = new PointItemSnapshot(
			itemId,
			item.getItemBasicInfo().getItemName(),
			item.getItemCode().getCode(),
			item.getItemMedia().getItemThumbNailUrl(),
			item.getItemPrice().getItemPrice()
		);

		OrderPointItemResponse orderPointItemResponse = pointItemOrderService.orderPointItem(
			new OrderPointItemCommand(
				memberSnapshot,
				itemSnapShot
			),
			orderPointItemRequest
		);

		return ApiTemplate.ok(PointItemResponseMessage.POINT_ITEM_ORDER_SUCCESS, orderPointItemResponse);

	}
}

package com.example.green.domain.pointshop.order.controller;

import static com.example.green.domain.pointshop.order.controller.OrderResponseMessage.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.common.idempotency.Idempotent;
import com.example.green.domain.pointshop.order.controller.docs.OrderControllerDocs;
import com.example.green.domain.pointshop.order.controller.dto.SingleOrderRequest;
import com.example.green.domain.pointshop.order.service.OrderService;
import com.example.green.domain.pointshop.order.service.command.SingleOrderCommand;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController implements OrderControllerDocs {

	private final OrderService orderService;

	@Idempotent
	@PostMapping("/point-products/single")
	@AuthenticatedApi
	public ApiTemplate<Long> exchangeSinglePointProduct(
		@RequestBody SingleOrderRequest dto,
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		SingleOrderCommand command =
			dto.toCommand(principal.getMemberId(), principal.getMemberKey(), principal.getEmail());
		Long result = orderService.orderSingleItem(command);
		return ApiTemplate.ok(POINT_PRODUCT_EXCHANGE_SUCCESS, result);
	}
}

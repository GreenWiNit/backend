package com.example.green.domain.pointshop.order.controller;

import static com.example.green.domain.pointshop.order.controller.OrderResponseMessage.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.order.controller.docs.OrderControllerDocs;
import com.example.green.domain.pointshop.order.controller.dto.SingleOrderRequest;
import com.example.green.domain.pointshop.order.service.OrderService;
import com.example.green.domain.pointshop.order.service.command.SingleOrderCommand;
import com.example.green.global.api.ApiTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController implements OrderControllerDocs {

	private final OrderService orderService;

	@PostMapping("/point-products/single")
	public ApiTemplate<Long> exchangeSinglePointProduct(@RequestBody SingleOrderRequest dto) {
		// TODO: 인증 시스템 구현 후 실제 사용자 정보로 변경
		SingleOrderCommand command = SingleOrderCommand.of(1L, "01ARZ3NDEKTSV4RRFFQ69G5FAV", dto);
		Long result = orderService.orderSingleItem(command);
		return ApiTemplate.ok(POINT_PRODUCT_EXCHANGE_SUCCESS, result);
	}
}

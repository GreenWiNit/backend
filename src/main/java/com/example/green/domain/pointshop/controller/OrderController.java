package com.example.green.domain.pointshop.controller;

import static com.example.green.domain.pointshop.controller.message.OrderResponseMessage.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.controller.dto.SingleOrderRequest;
import com.example.green.domain.pointshop.service.OrderService;
import com.example.green.domain.pointshop.service.command.SingleOrderCommand;
import com.example.green.global.api.ApiTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ApiTemplate<Long> exchangePointProduct(@RequestBody SingleOrderRequest dto) {
		// todo: security 추가 시 사용자 ID, 사용자 코드 추출
		SingleOrderCommand command = SingleOrderCommand.of(1L, "memberCode", dto);
		Long result = orderService.orderSingleItem(command);
		return ApiTemplate.ok(POINT_PRODUCT_EXCHANGE_SUCCESS, result);
	}
}

package com.example.green.domain.pointshop.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.controller.docs.OrderAdminControllerDocs;
import com.example.green.domain.pointshop.controller.message.OrderResponseMessage;
import com.example.green.domain.pointshop.repository.OrderQueryRepository;
import com.example.green.domain.pointshop.repository.dto.ExchangeApplicationResult;
import com.example.green.domain.pointshop.repository.dto.ExchangeApplicationSearchCondition;
import com.example.green.domain.pointshop.repository.dto.PointProductApplicantResult;
import com.example.green.domain.pointshop.service.OrderService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class OrderAdminController implements OrderAdminControllerDocs {

	private final OrderService orderService;
	private final OrderQueryRepository orderQueryRepository;

	@GetMapping("/point-products/{pointProductId}")
	public ApiTemplate<PageTemplate<PointProductApplicantResult>> getExchangeApplicant(
		@PathVariable Long pointProductId,
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false) Integer size
	) {
		PageTemplate<PointProductApplicantResult> result =
			orderQueryRepository.findExchangeApplicantByPointProduct(pointProductId, page, size);
		return ApiTemplate.ok(OrderResponseMessage.EXCHANGE_APPLICANT_INQUIRY_SUCCESS, result);
	}

	@GetMapping("/point-products")
	public ApiTemplate<PageTemplate<ExchangeApplicationResult>> searchExchangeApplication(
		@ParameterObject @ModelAttribute
		ExchangeApplicationSearchCondition condition
	) {
		PageTemplate<ExchangeApplicationResult> result = orderQueryRepository.searchExchangeApplication(condition);
		return ApiTemplate.ok(OrderResponseMessage.EXCHANGE_APPLICANT_INQUIRY_SUCCESS, result);
	}

	@PatchMapping("/{orderId}/shipping")
	public NoContent shipOrder(@PathVariable Long orderId) {
		orderService.shipOrder(orderId);
		return NoContent.ok(OrderResponseMessage.ORDER_SHIPPING_START_SUCCESS);
	}

	@PatchMapping("/{orderId}/complete")
	public NoContent completeDelivery(@PathVariable Long orderId) {
		orderService.completeDelivery(orderId);
		return NoContent.ok(OrderResponseMessage.ORDER_DELIVERY_COMPLETE_SUCCESS);
	}
}

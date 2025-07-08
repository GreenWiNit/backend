package com.example.green.domain.pointshop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.controller.docs.OrderAdminControllerDocs;
import com.example.green.domain.pointshop.controller.message.OrderResponseMessage;
import com.example.green.domain.pointshop.repository.OrderQueryRepository;
import com.example.green.domain.pointshop.repository.dto.PointProductApplicantResult;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class OrderAdminController implements OrderAdminControllerDocs {

	private final OrderQueryRepository orderQueryRepository;

	@GetMapping("point-products/{pointProductId}")
	public ApiTemplate<PageTemplate<PointProductApplicantResult>> getExchangeApplicant(
		@PathVariable Long pointProductId,
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false) Integer size
	) {
		PageTemplate<PointProductApplicantResult> result =
			orderQueryRepository.findExchangeApplicantByPointProduct(pointProductId, page, size);
		return ApiTemplate.ok(OrderResponseMessage.EXCHANGE_APPLICANT_INQUIRY_SUCCESS, result);
	}
}

package com.example.green.domain.pointshop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.controller.dto.PointProductsView;
import com.example.green.domain.pointshop.controller.message.PointProductResponseMessage;
import com.example.green.domain.pointshop.controller.query.PointProductQueryRepository;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class PointProductController {

	private final PointProductQueryRepository pointProductQueryRepository;

	@GetMapping
	public ApiTemplate<CursorTemplate<Long, PointProductsView>> getProducts(
		@RequestParam(required = false) Long cursor
	) {
		CursorTemplate<Long, PointProductsView> result = pointProductQueryRepository.getProductsByCursor(cursor);
		return ApiTemplate.ok(PointProductResponseMessage.POINT_PRODUCTS_INQUIRY_SUCCESS, result);
	}
}

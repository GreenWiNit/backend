package com.example.green.domain.pointshop.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.product.controller.docs.PointProductControllerDocs;
import com.example.green.domain.pointshop.product.controller.dto.PointProductDetail;
import com.example.green.domain.pointshop.product.controller.dto.PointProductView;
import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.repository.PointProductQueryRepository;
import com.example.green.domain.pointshop.product.service.PointProductQueryService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/point-products")
@RequiredArgsConstructor
public class PointProductController implements PointProductControllerDocs {

	private final PointProductQueryService pointProductQueryService;
	private final PointProductQueryRepository pointProductQueryRepository;

	@GetMapping
	public ApiTemplate<CursorTemplate<Long, PointProductView>> getProducts(
		@RequestParam(required = false) Long cursor
	) {
		CursorTemplate<Long, PointProductView> result = pointProductQueryRepository.getProductsByCursor(cursor);
		return ApiTemplate.ok(PointProductResponseMessage.POINT_PRODUCTS_INQUIRY_SUCCESS, result);
	}

	@GetMapping("/{pointProductId}")
	public ApiTemplate<PointProductDetail> getProductById(@PathVariable Long pointProductId) {
		PointProduct pointProduct = pointProductQueryService.getPointProduct(pointProductId);
		PointProductDetail result = PointProductDetail.from(pointProduct);
		return ApiTemplate.ok(PointProductResponseMessage.POINT_PRODUCT_DETAIL_INQUIRY_SUCCESS, result);
	}
}

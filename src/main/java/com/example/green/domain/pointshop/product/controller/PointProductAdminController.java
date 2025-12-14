package com.example.green.domain.pointshop.product.controller;

import static com.example.green.domain.pointshop.product.controller.PointProductResponseMessage.*;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.product.controller.docs.PointProductAdminControllerDocs;
import com.example.green.domain.pointshop.product.controller.dto.PointProductDetailForAdmin;
import com.example.green.domain.pointshop.product.controller.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchResult;
import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.repository.PointProductQueryRepository;
import com.example.green.domain.pointshop.product.service.PointProductQueryService;
import com.example.green.domain.pointshop.product.service.PointProductService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.security.annotation.AdminApi;
import com.example.green.infra.excel.core.ExcelDownloader;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/point-products")
@AdminApi
public class PointProductAdminController implements PointProductAdminControllerDocs {

	private final PointProductService pointProductService;
	private final PointProductQueryService pointProductQueryService;
	private final PointProductQueryRepository pointProductQueryRepository;
	private final ExcelDownloader excelDownloader;

	@GetMapping
	public ApiTemplate<PageTemplate<PointProductSearchResult>> findPointProducts(
		@ParameterObject @ModelAttribute PointProductSearchCondition condition
	) {
		PageTemplate<PointProductSearchResult> result = pointProductQueryRepository.searchPointProducts(condition);
		return ApiTemplate.ok(PointProductResponseMessage.POINT_PRODUCTS_INQUIRY_SUCCESS, result);
	}

	@GetMapping("/{pointProductId}")
	public ApiTemplate<PointProductDetailForAdmin> getProductById(@PathVariable Long pointProductId) {
		PointProduct pointProduct = pointProductQueryService.getPointProduct(pointProductId);
		PointProductDetailForAdmin result = PointProductDetailForAdmin.from(pointProduct);
		return ApiTemplate.ok(PointProductResponseMessage.POINT_PRODUCT_DETAIL_INQUIRY_SUCCESS, result);
	}

	@GetMapping("/excel")
	public void findPointProducts(
		@ParameterObject @ModelAttribute
		PointProductExcelCondition condition,
		HttpServletResponse response
	) {
		List<PointProductSearchResult> result = pointProductQueryRepository.searchPointProductsForExcel(condition);
		excelDownloader.downloadAsStream(result, response);
	}

	@PatchMapping("/{pointProductId}/show")
	public NoContent showDisplay(@PathVariable Long pointProductId) {
		pointProductService.showDisplay(pointProductId);
		return NoContent.ok(DISPLAY_SHOW_SUCCESS);
	}

	@PatchMapping("/{pointProductId}/hide")
	public NoContent hideDisplay(@PathVariable Long pointProductId) {
		pointProductService.hideDisplay(pointProductId);
		return NoContent.ok(DISPLAY_HIDE_SUCCESS);
	}
}

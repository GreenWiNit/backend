package com.example.green.domain.pointshop.product.controller;

import static com.example.green.domain.pointshop.product.controller.PointProductResponseMessage.*;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.product.controller.docs.PointProductAdminControllerDocs;
import com.example.green.domain.pointshop.product.controller.dto.PointProductCreateDto;
import com.example.green.domain.pointshop.product.controller.dto.PointProductDetail;
import com.example.green.domain.pointshop.product.controller.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchResult;
import com.example.green.domain.pointshop.product.controller.dto.PointProductUpdateDto;
import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.entity.vo.Media;
import com.example.green.domain.pointshop.product.entity.vo.Price;
import com.example.green.domain.pointshop.product.entity.vo.Stock;
import com.example.green.domain.pointshop.product.repository.PointProductQueryRepository;
import com.example.green.domain.pointshop.product.service.PointProductQueryService;
import com.example.green.domain.pointshop.product.service.PointProductService;
import com.example.green.domain.pointshop.product.service.command.PointProductCreateCommand;
import com.example.green.domain.pointshop.product.service.command.PointProductUpdateCommand;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.excel.core.ExcelDownloader;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/point-products")
public class PointProductAdminController implements PointProductAdminControllerDocs {

	private final PointProductService pointProductService;
	private final PointProductQueryService pointProductQueryService;
	private final PointProductQueryRepository pointProductQueryRepository;
	private final ExcelDownloader excelDownloader;

	@PostMapping
	public ApiTemplate<Long> createPointProduct(@RequestBody @Valid PointProductCreateDto dto) {
		PointProductCreateCommand command = dto.toCommand();
		Long result = pointProductService.create(command);
		return ApiTemplate.ok(POINT_PRODUCT_CREATION_SUCCESS, result);
	}

	@GetMapping
	public ApiTemplate<PageTemplate<PointProductSearchResult>> findPointProducts(
		@ParameterObject @ModelAttribute PointProductSearchCondition condition
	) {
		PageTemplate<PointProductSearchResult> result = pointProductQueryRepository.searchPointProducts(condition);
		return ApiTemplate.ok(PointProductResponseMessage.POINT_PRODUCTS_INQUIRY_SUCCESS, result);
	}

	@GetMapping("/{pointProductId}")
	public ApiTemplate<PointProductDetail> getProductById(@PathVariable Long pointProductId) {
		PointProduct pointProduct = pointProductQueryService.getPointProduct(pointProductId);
		PointProductDetail result = PointProductDetail.forAdmin(pointProduct);
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

	@PutMapping("/{pointProductId}")
	public NoContent updatePointProduct(
		@RequestBody @Valid PointProductUpdateDto dto,
		@PathVariable Long pointProductId
	) {
		PointProductUpdateCommand command = new PointProductUpdateCommand(
			new Code(dto.code()),
			new BasicInfo(dto.name(), dto.description()),
			new Media(dto.thumbnailUrl()),
			new Price(dto.price()),
			new Stock(dto.stock())
		);

		pointProductService.update(command, pointProductId);
		return NoContent.ok(POINT_PRODUCT_UPDATE_SUCCESS);
	}

	@DeleteMapping("/{pointProductId}")
	public NoContent deletePointProduct(@PathVariable Long pointProductId) {
		pointProductService.delete(pointProductId);
		return NoContent.ok(POINT_PRODUCT_DELETE_SUCCESS);
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

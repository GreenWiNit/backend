package com.example.green.domain.pointshop.controller;

import static com.example.green.domain.pointshop.controller.message.PointProductResponseMessage.*;

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

import com.example.green.domain.pointshop.controller.docs.PointProductControllerDocs;
import com.example.green.domain.pointshop.controller.dto.PointProductCreateDto;
import com.example.green.domain.pointshop.controller.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchResponse;
import com.example.green.domain.pointshop.controller.dto.PointProductUpdateDto;
import com.example.green.domain.pointshop.controller.query.PointProductQueryRepository;
import com.example.green.domain.pointshop.entity.pointproduct.vo.BasicInfo;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Code;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Media;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Price;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Stock;
import com.example.green.domain.pointshop.service.PointProductService;
import com.example.green.domain.pointshop.service.command.PointProductCreateCommand;
import com.example.green.domain.pointshop.service.command.PointProductUpdateCommand;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.excel.core.ExcelDownloader;
import com.example.green.global.security.annotation.AdminApi;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@AdminApi
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point-products")
public class PointProductAdminController implements PointProductControllerDocs {

	private final PointProductService pointProductService;
	private final PointProductQueryRepository pointProductQueryRepository;
	private final ExcelDownloader excelDownloader;

	@PostMapping
	public ApiTemplate<Long> createPointProduct(@RequestBody @Valid PointProductCreateDto dto) {
		PointProductCreateCommand command = new PointProductCreateCommand(
			new Code(dto.code()),
			new BasicInfo(dto.name(), dto.description()),
			new Media(dto.thumbnailUrl()),
			new Price(dto.price()),
			new Stock(dto.stock())
		);

		Long result = pointProductService.create(command);
		return ApiTemplate.ok(POINT_PRODUCT_CREATION_SUCCESS, result);
	}

	@GetMapping
	public ApiTemplate<PageTemplate<PointProductSearchResponse>> findPointProducts(
		@ParameterObject @ModelAttribute PointProductSearchCondition condition
	) {
		PageTemplate<PointProductSearchResponse> result = pointProductQueryRepository.searchPointProducts(condition);
		return ApiTemplate.ok(POINT_PRODUCTS_SEARCH_SUCCESS, result);
	}

	@GetMapping("/excel")
	public void findPointProducts(
		@ParameterObject @ModelAttribute
		PointProductExcelCondition condition,
		HttpServletResponse response
	) {
		List<PointProductSearchResponse> result = pointProductQueryRepository.searchPointProductsForExcel(condition);
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

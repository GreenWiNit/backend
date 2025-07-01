package com.example.green.domain.pointshop.controller;

import static com.example.green.domain.pointshop.controller.message.PointProductResponseMessage.*;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.controller.docs.PointProductControllerDocs;
import com.example.green.domain.pointshop.controller.dto.PointProductCreateDto;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchResponse;
import com.example.green.domain.pointshop.controller.query.PointProductQueryRepository;
import com.example.green.domain.pointshop.entity.pointproduct.vo.BasicInfo;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Media;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Price;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Stock;
import com.example.green.domain.pointshop.service.PointProductService;
import com.example.green.domain.pointshop.service.command.PointProductCreateCommand;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point-products")
public class PointProductAdminController implements PointProductControllerDocs {

	private final PointProductService pointProductService;
	private final PointProductQueryRepository pointProductQueryRepository;

	@PostMapping
	public ApiTemplate<Long> createPointProduct(@RequestBody @Valid PointProductCreateDto dto) {
		PointProductCreateCommand command = new PointProductCreateCommand(
			new BasicInfo(dto.code(), dto.name(), dto.description()),
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
}

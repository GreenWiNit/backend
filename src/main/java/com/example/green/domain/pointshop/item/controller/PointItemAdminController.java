package com.example.green.domain.pointshop.item.controller;

import static com.example.green.domain.pointshop.item.controller.message.PointItemResponseMessage.*;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.item.controller.docs.PointItemAdminControllerDocs;
import com.example.green.domain.pointshop.item.controller.message.PointItemResponseMessage;
import com.example.green.domain.pointshop.item.dto.request.PointItemExcelDownloadRequest;
import com.example.green.domain.pointshop.item.dto.request.PointItemSearchRequest;
import com.example.green.domain.pointshop.item.dto.response.ItemWithApplicantsDTO;
import com.example.green.domain.pointshop.item.dto.response.PointItemAdminResponse;
import com.example.green.domain.pointshop.item.dto.response.PointItemSearchResponse;
import com.example.green.domain.pointshop.item.repository.PointItemOrderRepository;
import com.example.green.domain.pointshop.item.repository.PointItemQueryRepository;
import com.example.green.domain.pointshop.item.service.PointItemQueryService;
import com.example.green.domain.pointshop.item.service.PointItemService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.security.annotation.AdminApi;
import com.example.green.infra.excel.core.ExcelDownloader;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/point-items")
@AdminApi
public class PointItemAdminController implements PointItemAdminControllerDocs {

	private final PointItemService pointItemService;
	private final PointItemQueryService pointItemQueryService;
	private final PointItemQueryRepository pointItemQueryRepository;
	private final PointItemOrderRepository pointItemOrderRepository;
	private final ExcelDownloader excelDownloader;

	@GetMapping
	public ApiTemplate<PageTemplate<PointItemSearchResponse>> findPointItems(
		@ParameterObject @ModelAttribute PointItemSearchRequest request
	) {
		PageTemplate<PointItemSearchResponse> result = pointItemQueryRepository.searchPointItems(request);
		return ApiTemplate.ok(POINT_ITEMS_INQUIRY_SUCCESS, result);
	}

	@GetMapping("/{pointItemId}")
	public ApiTemplate<PointItemAdminResponse> showPointItem(@PathVariable Long pointItemId) {
		PointItemAdminResponse response = pointItemQueryService.getPointItemAdminResponse(pointItemId);
		return ApiTemplate.ok(PointItemResponseMessage.POINT_ITEM_LOAD_SUCCESS, response);
	}

	@PatchMapping("/{pointItemId}/show")
	public NoContent showPointItemDisplay(@PathVariable Long pointItemId) {
		pointItemService.showItemDisplay(pointItemId);
		return NoContent.ok(DISPLAY_SHOW_ITEM_SUCCESS);
	}

	@PatchMapping("/{pointItemId}/hide")
	public NoContent hidePointItemDisplay(@PathVariable Long pointItemId) {
		pointItemService.hideItemDisplay(pointItemId);
		return NoContent.ok(DISPLAY_HIDE_ITEM_SUCCESS);
	}

	@GetMapping("/excel")
	public void downloadPointItemsExcel(
		@ParameterObject @ModelAttribute
		PointItemExcelDownloadRequest pointItemExcelDownloadRequest,
		HttpServletResponse response
	) {
		List<PointItemSearchResponse> result = pointItemQueryRepository.searchPointItemsForExcel(
			pointItemExcelDownloadRequest);
		excelDownloader.downloadAsStream(result, response);
	}

	@GetMapping("/orders")
	public ApiTemplate<PageTemplate<ItemWithApplicantsDTO>> findAllOrders(
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false) Integer size) {

		PageTemplate<ItemWithApplicantsDTO> result =
			pointItemOrderRepository.findAllItemsWithApplicants(page, size);

		return ApiTemplate.ok(LOAD_ALL_ITEMS, result);

	}
}

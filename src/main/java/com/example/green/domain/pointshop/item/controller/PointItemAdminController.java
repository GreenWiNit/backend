package com.example.green.domain.pointshop.item.controller;

import static com.example.green.domain.pointshop.item.controller.message.PointItemResponseMessage.*;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.item.controller.docs.PointItemAdminControllerDocs;
import com.example.green.domain.pointshop.item.controller.message.PointItemResponseMessage;
import com.example.green.domain.pointshop.item.dto.request.CreatePointItemRequest;
import com.example.green.domain.pointshop.item.dto.request.PointItemExcelDownloadRequest;
import com.example.green.domain.pointshop.item.dto.request.PointItemSearchRequest;
import com.example.green.domain.pointshop.item.dto.request.UpdatePointItemRequest;
import com.example.green.domain.pointshop.item.dto.response.ItemWithApplicantsDTO;
import com.example.green.domain.pointshop.item.dto.response.PointItemAdminResponse;
import com.example.green.domain.pointshop.item.dto.response.PointItemSearchResponse;
import com.example.green.domain.pointshop.item.entity.vo.ItemBasicInfo;
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;
import com.example.green.domain.pointshop.item.entity.vo.ItemMedia;
import com.example.green.domain.pointshop.item.entity.vo.ItemPrice;
import com.example.green.domain.pointshop.item.entity.vo.ItemStock;
import com.example.green.domain.pointshop.item.repository.PointItemOrderRepository;
import com.example.green.domain.pointshop.item.repository.PointItemQueryRepository;
import com.example.green.domain.pointshop.item.service.PointItemQueryService;
import com.example.green.domain.pointshop.item.service.PointItemService;
import com.example.green.domain.pointshop.item.service.command.PointItemCreateCommand;
import com.example.green.domain.pointshop.item.service.command.PointItemUpdateCommand;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.security.annotation.AdminApi;
import com.example.green.infra.excel.core.ExcelDownloader;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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

	@PostMapping
	public ApiTemplate<Long> createPointItem(@RequestBody @Valid CreatePointItemRequest createPointItemRequest) {
		PointItemCreateCommand command = createPointItemRequest.toCommand();
		Long result = pointItemService.create(command);
		return ApiTemplate.ok(PointItemResponseMessage.POINT_ITEM_CREATION_SUCCESS, result);
	}

	@GetMapping
	public ApiTemplate<PageTemplate<PointItemSearchResponse>> findPointItems(
		@ParameterObject @ModelAttribute PointItemSearchRequest request
	) {
		PageTemplate<PointItemSearchResponse> result = pointItemQueryRepository.searchPointItems(request);
		return ApiTemplate.ok(POINT_ITEMS_INQUIRY_SUCCESS, result);
	}

	@PutMapping("/{pointItemId}")
	public NoContent updatePointItem(
		@RequestBody @Valid UpdatePointItemRequest updatePointItemRequest,
		@PathVariable Long pointItemId
	) {
		PointItemUpdateCommand command = new PointItemUpdateCommand(
			new ItemCode(updatePointItemRequest.code()),
			new ItemBasicInfo(updatePointItemRequest.name(), updatePointItemRequest.description()),
			new ItemMedia(updatePointItemRequest.thumbnailUrl()),
			new ItemPrice(updatePointItemRequest.price()),
			new ItemStock(updatePointItemRequest.stock())
		);

		pointItemService.updatePointItem(command, pointItemId);

		return NoContent.ok(POINT_ITEM_UPDATE_SUCCESS);
	}

	@GetMapping("/{pointItemId}")
	public ApiTemplate<PointItemAdminResponse> showPointItem(@PathVariable Long pointItemId) {
		PointItemAdminResponse response = pointItemQueryService.getPointItemAdminResponse(pointItemId);
		return ApiTemplate.ok(PointItemResponseMessage.POINT_ITEM_LOAD_SUCCESS, response);
	}

	@DeleteMapping("/{pointItemId}")
	public NoContent deletePointItem(@PathVariable Long pointItemId) {
		pointItemService.delete(pointItemId);
		return NoContent.ok(POINT_ITEM_DELETE_SUCCESS);
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

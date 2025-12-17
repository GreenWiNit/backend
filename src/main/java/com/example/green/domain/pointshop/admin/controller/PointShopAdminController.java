package com.example.green.domain.pointshop.admin.controller;

import static com.example.green.domain.pointshop.admin.controller.message.PointShopAdminResponseMessage.*;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.admin.controller.docs.PointShopAdminControllerDocs;
import com.example.green.domain.pointshop.admin.dto.request.AdminCreatePointShopRequest;
import com.example.green.domain.pointshop.admin.dto.request.AdminUpdatePointShopRequest;
import com.example.green.domain.pointshop.admin.service.PointShopAdminService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.security.annotation.AdminApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/point-shop")
@AdminApi
public class PointShopAdminController implements PointShopAdminControllerDocs {

	private final PointShopAdminService pointShopAdminService;

	@PostMapping("/create")
	public ApiTemplate<Long> create(@RequestBody @Valid AdminCreatePointShopRequest createPointShopRequest) {
		Long result = pointShopAdminService.create(createPointShopRequest);
		return ApiTemplate.ok(POINT_ITEM_CREATION_SUCCESS, result);
	}

	@PutMapping("/update/{id}")
	public NoContent update(
		@PathVariable Long id,
		@RequestBody @Valid AdminUpdatePointShopRequest updatePointShopRequest
	) {
		pointShopAdminService.update(updatePointShopRequest, id);
		return NoContent.ok(POINT_ITEM_UPDATE_SUCCESS);
	}

	@DeleteMapping("/delete/{id}")
	public NoContent delete(@PathVariable Long id) {
		pointShopAdminService.delete(id);
		return NoContent.ok(POINT_ITEM_DELETE_SUCCESS);
	}
}

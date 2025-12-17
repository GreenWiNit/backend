package com.example.green.domain.pointshop.admin.controller;

import static com.example.green.domain.pointshop.admin.controller.message.PointShopAdminResponseMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.pointshop.admin.dto.request.AdminCreatePointShopRequest;
import com.example.green.domain.pointshop.admin.dto.request.AdminUpdatePointShopRequest;
import com.example.green.domain.pointshop.admin.service.PointShopAdminService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.template.base.BaseControllerUnitTest;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(PointShopAdminController.class)
class PointShopAdminControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private PointShopAdminService pointShopAdminService;

	@Test
	void 포인트샵_생성_요청에_성공한다() {
		// given
		AdminCreatePointShopRequest request = AdminCreatePointShopRequest.builder()
			.code("ITEM001")
			.name("테스트 상품")
			.description("테스트 설명")
			.thumbnailUrl("https://test.com/image.png")
			.price(BigDecimal.valueOf(1000))
			.build();

		when(pointShopAdminService.create(any(AdminCreatePointShopRequest.class)))
			.thenReturn(1L);

		// when
		ApiTemplate<Long> response = create(request);

		// then
		assertThat(response.result()).isEqualTo(1L);
		assertThat(response.message())
			.isEqualTo(POINT_ITEM_CREATION_SUCCESS.getMessage());
	}

	public static ApiTemplate<Long> create(AdminCreatePointShopRequest request) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/admin/point-shop/create")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	@Test
	void 포인트샵_수정_요청에_성공한다() {
		// given
		AdminUpdatePointShopRequest request = AdminUpdatePointShopRequest.builder()
			.code("ITEM001")
			.name("테스트 상품")
			.description("테스트 설명")
			.thumbnailUrl("https://test.com/image.png")
			.price(BigDecimal.valueOf(1000))
			.build();

		doNothing().when(pointShopAdminService)
			.update(any(AdminUpdatePointShopRequest.class), anyLong());

		// when
		NoContent response = update(1L, request);

		// then
		assertThat(response.message())
			.isEqualTo(POINT_ITEM_UPDATE_SUCCESS.getMessage());
	}

	public static NoContent update(Long id, AdminUpdatePointShopRequest request) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().put("/api/admin/point-shop/update/" + id)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

	@Test
	void 포인트샵_삭제_요청에_성공한다() {
		// given
		doNothing().when(pointShopAdminService).delete(anyLong());

		// when
		NoContent response = delete(1L);

		// then
		assertThat(response.message())
			.isEqualTo(POINT_ITEM_DELETE_SUCCESS.getMessage());
	}

	public static NoContent delete(Long id) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().delete("/api/admin/point-shop/delete/" + id)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}
}

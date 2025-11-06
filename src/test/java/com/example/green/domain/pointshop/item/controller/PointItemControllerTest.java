package com.example.green.domain.pointshop.item.controller;

import static com.example.green.domain.pointshop.item.controller.message.PointItemResponseMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.pointshop.item.dto.response.PointItemResponse;
import com.example.green.domain.pointshop.item.repository.PointItemQueryRepository;
import com.example.green.domain.pointshop.item.service.PointItemQueryService;
import com.example.green.domain.pointshop.item.service.PointItemService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.template.base.BaseControllerUnitTest;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(PointItemController.class)
class PointItemControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private PointItemQueryService pointItemQueryService;

	@MockitoBean
	private PointItemService pointItemService;

	@MockitoBean
	private PointItemQueryRepository pointItemQueryRepository;

	@Test
	void 커서_기반_아이템_목록_조회에_성공한다() {
		// given
		PointItemResponse mock = mock(PointItemResponse.class);
		CursorTemplate<Long, PointItemResponse> mockResult = CursorTemplate.of(List.of(mock));
		when(pointItemQueryRepository.getPointItemsByCursor(anyLong())).thenReturn(mockResult);

		// when
		ApiTemplate<CursorTemplate<Long, PointItemResponse>> response = getItems(1L);

		// then
		assertThat(response.result()).isEqualTo(mockResult);
		assertThat(response.message()).isEqualTo(POINT_ITEMS_INQUIRY_SUCCESS.getMessage());
	}

	public static ApiTemplate<CursorTemplate<Long, PointItemResponse>> getItems(Long cursor) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.param("cursor", cursor)
			.when().get("/api/point-items")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});
	}

}

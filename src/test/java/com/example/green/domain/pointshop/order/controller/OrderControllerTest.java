package com.example.green.domain.pointshop.order.controller;

import static com.example.green.domain.pointshop.order.controller.OrderResponseMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.pointshop.order.service.OrderService;
import com.example.green.domain.pointshop.order.service.command.SingleOrderCommand;
import com.example.green.domain.pointshop.order.controller.dto.SingleOrderRequest;
import com.example.green.global.api.ApiTemplate;
import com.example.green.template.base.BaseControllerUnitTest;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private OrderService orderService;

	@Test
	void 단일_주문_정보로_상품교환_요청시_단일_상품이_주문된다() {
		// given
		SingleOrderRequest dto = new SingleOrderRequest(1L, 1L, 1);
		when(orderService.orderSingleItem(any(SingleOrderCommand.class))).thenReturn(1L);

		// when
		ApiTemplate<Long> response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(dto)
			.when().post("/api/orders/point-products/single")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});

		// then
		assertThat(response.message()).isEqualTo(POINT_PRODUCT_EXCHANGE_SUCCESS.getMessage());
		assertThat(response.result()).isEqualTo(1L);
	}

}
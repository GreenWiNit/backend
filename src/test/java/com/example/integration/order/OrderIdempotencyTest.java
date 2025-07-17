package com.example.integration.order;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import com.example.green.domain.pointshop.order.controller.dto.SingleOrderRequest;
import com.example.green.domain.pointshop.order.entity.Order;
import com.example.green.domain.pointshop.order.repository.OrderRepository;
import com.example.green.global.api.ApiTemplate;
import com.example.integration.common.BaseIntegrationTest;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@Import(OrderTestConfig.class)
class OrderIdempotencyTest extends BaseIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		RestAssuredMockMvc.mockMvc(mockMvc);
	}

	@Test
	void 동일한_멱등키로_주문_요청_세_번을_할_경우_한_건만_처리된다() {
		// given
		SingleOrderRequest orderRequest = new SingleOrderRequest(1L, 1L, 1);

		// when
		ApiTemplate<Long> firstResponse = requestOrder(orderRequest);
		ApiTemplate<Long> secondResponse = requestOrder(orderRequest);
		ApiTemplate<Long> thirdResponse = requestOrder(orderRequest);

		// then
		List<Order> all = orderRepository.findAll();
		assertThat(all).hasSize(1);
		assertThat(firstResponse.result()).isOne();
		assertThat(secondResponse.result()).isOne();
		assertThat(thirdResponse.result()).isOne();
	}

	private static ApiTemplate<Long> requestOrder(SingleOrderRequest orderRequest) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(ContentType.JSON)
			.header("idempotency-Key", "unique-idempotency-key")
			.body(orderRequest)
			.when()
			.post("/api/orders/point-products/single")
			.then().log().all()
			.statusCode(200)
			.extract()
			.as(new TypeRef<>() {
			});
	}
}
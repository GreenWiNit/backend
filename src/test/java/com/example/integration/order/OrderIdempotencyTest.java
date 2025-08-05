package com.example.integration.order;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.green.domain.pointshop.order.controller.dto.SingleOrderRequest;
import com.example.green.domain.pointshop.order.entity.Order;
import com.example.green.domain.pointshop.order.repository.OrderRepository;
import com.example.green.global.api.ApiTemplate;
import com.example.integration.common.BaseIntegrationTest;
import com.example.integration.common.concurrency.ConcurrencyTestResult;
import com.example.integration.common.concurrency.ConcurrencyTestTemplate;
import com.example.integration.config.TestTokenServiceConfig;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@Import({OrderTestConfig.class, TestTokenServiceConfig.class})
class OrderIdempotencyTest extends BaseIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderDataSource dataSource;

	@BeforeEach
	void setUp() {
		RestAssuredMockMvc.mockMvc(mockMvc);
		dataSource.deleteOrderItems();
		dataSource.deleteOrder();
		dataSource.deleteIdempotency();

		dataSource.createOrder();
		dataSource.createOrderItems();
		dataSource.createIdempotency();
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

	@Test
	void 동일한_멱등키로_동시_주문_요청_세_번을_할_경우_한_건만_처리된다() throws InterruptedException {
		// given
		SingleOrderRequest orderRequest = new SingleOrderRequest(1L, 1L, 1);

		// when & then
		ConcurrencyTestResult result = ConcurrencyTestTemplate.build()
			.threadCount(3)
			.timeout(10)
			.execute(() -> {
				ApiTemplate<Long> response = requestOrder(orderRequest);
				assertThat(response.result()).isOne();
			});

		List<Order> all = orderRepository.findAll();
		assertThat(all).hasSize(1);
		assertThat(result.allSucceeded()).isTrue();
	}

	private static ApiTemplate<Long> requestOrder(SingleOrderRequest orderRequest) {
		return RestAssuredMockMvc
			.given().log().all()
			.contentType(ContentType.JSON)
			.header("Authorization", "Bearer TEST")
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
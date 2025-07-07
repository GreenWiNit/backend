package com.example.green.domain.point.controller;

import static com.example.green.domain.point.controller.message.PointTransactionResponseMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.point.entity.vo.TransactionType;
import com.example.green.domain.point.repository.PointTransactionQueryRepository;
import com.example.green.domain.point.repository.dto.MemberPointSummary;
import com.example.green.domain.point.repository.dto.MyPointTransaction;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.template.base.BaseControllerUnitTest;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(PointTransactionController.class)
class PointTransactionControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private PointTransactionQueryRepository pointTransactionQueryRepository;

	@Test
	void 내_포인트_조회_요청에_성공한다() {
		// given
		MemberPointSummary mock = new MemberPointSummary(BigDecimal.ONE, BigDecimal.TWO, BigDecimal.TEN);
		when(pointTransactionQueryRepository.findMemberPointSummary(anyLong())).thenReturn(mock);

		// when
		ApiTemplate<MemberPointSummary> response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.when().get("/api/points/me")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});

		// then
		assertThat(response.result()).usingRecursiveComparison().isEqualTo(mock);
		assertThat(response.message()).isEqualTo(MY_POINT_INQUIRY_SUCCESS.getMessage());
	}

	@Test
	void 포인트_내역_조회_요청에_성공한다() {
		// given
		List<MyPointTransaction> mock = List.of(
			new MyPointTransaction(1L, "reason", BigDecimal.ZERO, TransactionType.EARN, LocalDateTime.now())
		);
		CursorTemplate<Long, MyPointTransaction> mockResult = CursorTemplate.of(mock);
		when(pointTransactionQueryRepository.getPointTransaction(anyLong(), anyLong(), any(TransactionType.class)))
			.thenReturn(mockResult);

		// when
		ApiTemplate<CursorTemplate<Long, MyPointTransaction>> response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.queryParam("cursor", 1L)
			.queryParam("status", "earn")
			.when().get("/api/points/transaction")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});

		// then
		assertThat(response.result()).usingRecursiveComparison().isEqualTo(mockResult);
		assertThat(response.message()).isEqualTo(POINT_TRANSACTION_INQUIRY_SUCCESS.getMessage());
	}
}
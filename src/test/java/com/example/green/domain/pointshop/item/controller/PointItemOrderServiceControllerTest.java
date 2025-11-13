package com.example.green.domain.pointshop.item.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.green.domain.pointshop.item.dto.response.OrderPointItemResponse;
import com.example.green.domain.pointshop.item.entity.PointItem;
import com.example.green.domain.pointshop.item.entity.vo.ItemBasicInfo;
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;
import com.example.green.domain.pointshop.item.entity.vo.ItemMedia;
import com.example.green.domain.pointshop.item.entity.vo.ItemPrice;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;
import com.example.green.domain.pointshop.item.service.PointItemOrderService;
import com.example.green.domain.pointshop.item.service.command.OrderPointItemCommand;
import com.example.green.template.base.BaseControllerUnitTest;

@WebMvcTest(PointItemOrderController.class)
class PointItemOrderServiceControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private PointItemOrderService pointItemOrderService;

	@MockitoBean
	private PointItemRepository pointItemRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser
	void 포인트로_아이템_교환에_성공한다() throws Exception {
		// given
		Long itemId = 1L;

		ItemCode itemCode = new ItemCode("ITM-AA-002");
		ItemBasicInfo itemBasicInfo = new ItemBasicInfo("무지개", "행운의 네잎클로버가 싱그럽게 피어나요. 오늘 하루도 행운 가득");
		ItemMedia itemMedia = new ItemMedia("https://thumbnail.url/rainbow-pot.jpg");
		ItemPrice itemPrice = new ItemPrice(BigDecimal.valueOf(400));

		PointItem pointItem = new PointItem(itemCode, itemBasicInfo, itemMedia, itemPrice);

		when(pointItemRepository.findById(itemId)).thenReturn(Optional.of(pointItem));

		OrderPointItemResponse response = new OrderPointItemResponse(
			1L,
			"무지개",
			"https://thumbnail.url/rainbow-pot.jpg",
			BigDecimal.valueOf(400)
		);
		when(pointItemOrderService.orderPointItem(any(OrderPointItemCommand.class))).thenReturn(response);

		// when & then
		mockMvc.perform(post("/api/point-items/order/{id}", itemId)
				.contentType(MediaType.APPLICATION_JSON)
				.principal(() -> "testUser"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("아이템 교환이 완료되었습니다"))
			.andExpect(jsonPath("$.result.itemName").value("무지개"));
	}
}

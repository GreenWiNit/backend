package com.example.green.domain.dashboard.growth.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.green.domain.dashboard.growth.dto.request.ChangePositionRequest;
import com.example.green.domain.dashboard.growth.dto.response.ChangePositionGrowthItemResponse;
import com.example.green.domain.dashboard.growth.dto.response.GetPlantGrowthItemResponse;
import com.example.green.domain.dashboard.growth.dto.response.LoadGrowthResponse;
import com.example.green.domain.dashboard.growth.entity.Growth;
import com.example.green.domain.dashboard.growth.entity.PlantGrowthItem;
import com.example.green.domain.dashboard.growth.entity.enums.Level;
import com.example.green.domain.dashboard.growth.message.GrowthResponseMessage;
import com.example.green.domain.dashboard.growth.service.GrowthService;
import com.example.green.domain.dashboard.growth.service.PlantItemService;
import com.example.green.domain.member.entity.Member;
import com.example.green.template.base.BaseControllerUnitTest;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(GrowthController.class)
class GrowthControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private GrowthService growthService;

	@MockitoBean
	private PlantItemService plantItemService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Growth growth;
	private Member member;
	private PlantGrowthItem plantGrowthItem;

	@BeforeEach
	void setUp() {
		member = Member.create(
			"mb-1234",
			"이지은",
			"user1@test.com",
			"nickname"
		);

		growth = Growth.create(
			Level.SOIL,
			BigDecimal.valueOf(40),
			BigDecimal.valueOf(250),
			Level.SPROUT,
			member
		);

		plantGrowthItem = PlantGrowthItem.create(
			member,
			"맑은 뭉게 구름",
			"https://my-plant-growth-bucket.s3.ap-northeast-2.amazonaws.com/images/sunflower_growth_1.jpg"
		);

	}

	@Test
	@WithMockUser
	void 성장_데이터_조회_성공() throws Exception {
		Long memberId = 1L;

		LoadGrowthResponse loadGrowthResponse = new LoadGrowthResponse(
			memberId,
			growth.getGoalLevel(),
			growth.getLevel(),
			growth.getProgress(),
			growth.getRequiredPoint()
		);

		when(growthService.loadGrowth(anyLong())).thenReturn(loadGrowthResponse);

		mockMvc.perform(get("/api/dashboard/growth")
				.contentType(MediaType.APPLICATION_JSON)
				.principal(() -> "testUser"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.goalLevel").value(growth.getGoalLevel().name()))
			.andExpect(jsonPath("$.result.currentLevel").value(growth.getLevel().name()))
			.andExpect(jsonPath("$.result.nextLevelPercent").value(growth.getProgress().intValue()))
			.andExpect(jsonPath("$.result.nextLevelPoint").value(growth.getRequiredPoint().intValue()))
			.andExpect(jsonPath("$.message").value(
				(GrowthResponseMessage.LOAD_GROWTH_SUCCESS).getMessage())); // GrowthResponseMessage.LOAD_GROWTH_SUCCESS
	}

	@Test
	@WithMockUser
	void 포인트상점에서_조회한_아이템() throws Exception {
		Long memberId = 1L;

		GetPlantGrowthItemResponse getPlantGrowthItemResponse = new GetPlantGrowthItemResponse(
			plantGrowthItem.getItemName(),
			plantGrowthItem.getItemImgUrl(),
			plantGrowthItem.isApplicability()
		);

		when(plantItemService.getPlantGrowthItems(anyLong()))
			.thenReturn(List.of(getPlantGrowthItemResponse));

		mockMvc.perform(get("/api/dashboard/growth/items")
				.contentType(MediaType.APPLICATION_JSON)
				.principal(() -> "testUser"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result[0].itemName").value(plantGrowthItem.getItemName()))
			.andExpect(jsonPath("$.result[0].itemImgUrl").value(plantGrowthItem.getItemImgUrl()))
			.andExpect(jsonPath("$.result[0].applicability").value(plantGrowthItem.isApplicability()))
			.andExpect(jsonPath("$.message").value(GrowthResponseMessage.LOAD_ITEMS_SUCCESS.getMessage()));
	}

	@Test
	@WithMockUser
	void 아이템_장착여부_변경_성공() throws Exception {
		Long itemId = 1L;

		// plantItemService.changeApplicability는 void 메서드이므로 별도로 return 필요 없음
		doNothing().when(plantItemService).changeApplicability(anyLong(), eq(itemId));

		mockMvc.perform(patch("/api/dashboard/growth/{itemId}/applicability", itemId)
				.contentType(MediaType.APPLICATION_JSON)
				.principal(() -> "testUser"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value(GrowthResponseMessage.CHANGE_APPLICABILITY.getMessage()));
	}

	@Test
	@WithMockUser
	void 아이템_위치_변경_성공() throws Exception {
		Long itemId = 1L;
		ChangePositionRequest request = new ChangePositionRequest(100, 200);

		ChangePositionGrowthItemResponse response = new ChangePositionGrowthItemResponse(
			"맑은 뭉게 구름",
			"https://my-plant-growth-bucket.s3.ap-northeast-2.amazonaws.com/images/sunflower_growth_1.jpg",
			true,
			100,
			200
		);

		when(plantItemService.changePositionGrowthItem(anyLong(), eq(itemId), any(ChangePositionRequest.class)))
			.thenReturn(response);

		mockMvc.perform(patch("/api/dashboard/growth/{itemId}/position", itemId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.principal(() -> "testUser"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.itemName").value(response.itemName()))
			.andExpect(jsonPath("$.result.itemImgUrl").value(response.itemImgUrl()))
			.andExpect(jsonPath("$.result.applicability").value(response.applicability()))
			.andExpect(jsonPath("$.result.positionX").value(response.positionX()))
			.andExpect(jsonPath("$.result.positionY").value(response.positionY()))
			.andExpect(jsonPath("$.message").value(GrowthResponseMessage.CHANGE_POSITION_SUCCESS.getMessage()));
	}

}

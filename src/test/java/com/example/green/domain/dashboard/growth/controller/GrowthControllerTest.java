package com.example.green.domain.dashboard.growth.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.dashboard.growth.dto.response.LoadGrowthResponse;
import com.example.green.domain.dashboard.growth.entity.enums.Level;
import com.example.green.domain.dashboard.growth.message.GrowthResponseMessage;
import com.example.green.domain.dashboard.growth.service.GrowthService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.template.base.BaseControllerUnitTest;

@WebMvcTest(GrowthController.class)
class GrowthControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private GrowthService growthService;

	@Test
	@DisplayName("성장 데이터 조회 API 성공")
	@WithMockUser
	void getGrowthSuccess() {
		//given
		LoadGrowthResponse growthResponse = new LoadGrowthResponse(1L, Level.TREE, Level.SAPLING,
			BigDecimal.valueOf(3000), BigDecimal.valueOf(2500));
		Mockito.when(growthService.loadGrowth(anyLong()))
			.thenReturn(growthResponse);

		PrincipalDetails principal = new PrincipalDetails(
			3L, "google_123456789", "USER", "이지은", "test@test.com");

		ApiTemplate<LoadGrowthResponse> response =
			new GrowthController(growthService)
				.getGrowth(principal);

		assertThat(response).isNotNull();
		assertThat(response.result().goalLevel()).isEqualTo(Level.TREE);
		assertThat(response.result().currentLevel()).isEqualTo(Level.SAPLING);
		assertThat(response.message()).isEqualTo(GrowthResponseMessage.LOAD_GROWTH_SUCCESS.getMessage());

	}
}

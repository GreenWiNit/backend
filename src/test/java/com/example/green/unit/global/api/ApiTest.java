package com.example.green.unit.global.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ApiTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new ApiDummyController()).build();
	}

	@Test
	void API_응답을_가져온다() throws Exception {
		mockMvc.perform(get("/api-response"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("성공"))
			.andExpect(jsonPath("$.result").value("OK"));
	}

	@Test
	void 콘텐츠가_없을_경우_메시지만_응답받는다() throws Exception {
		mockMvc.perform(get("/no-content"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("성공"));
	}

}
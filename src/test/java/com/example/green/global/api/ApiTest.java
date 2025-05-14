package com.example.green.global.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApiDummyController.class)
class ApiTest {

	@Autowired
	private MockMvc mockMvc;

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
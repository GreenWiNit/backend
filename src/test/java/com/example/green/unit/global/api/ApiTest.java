package com.example.green.unit.global.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.green.unit.dummy.ApiDummyController;
import com.example.green.unit.dummy.ValidationDto;
import com.fasterxml.jackson.databind.ObjectMapper;

class ApiTest {

	private MockMvc mockMvc;
	private ObjectMapper objectMapper = new ObjectMapper();

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

	@Test
	void ModelAttribute_파싱은_특정_필드가_없어도_성공한다() throws Exception {
		mockMvc.perform(
				get("/valid-model-attribute")
					.queryParam("test", "test")
					.queryParam("age", "1")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("성공"));
	}

	@Test
	void RequestBody_파싱이_성공한다() throws Exception {
		// given
		ValidationDto dto = new ValidationDto("test", 1, true);
		String content = objectMapper.writeValueAsString(dto);

		// when & then
		mockMvc.perform(
				post("/valid-request-body")
					.contentType(MediaType.APPLICATION_JSON)
					.content(content)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("성공"));
	}

}
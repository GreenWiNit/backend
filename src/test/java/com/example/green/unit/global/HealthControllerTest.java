package com.example.green.unit.global;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.green.global.HealthController;

public class HealthControllerTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new HealthController()).build();
	}

	@Test
	public void 헬스_테스트() throws Exception {
		mockMvc.perform(get("/health-check"))
			.andExpect(status().isOk())
			.andExpect(content().string("OK"));
	}
}
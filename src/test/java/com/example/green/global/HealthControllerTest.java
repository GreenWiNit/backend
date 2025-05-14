package com.example.green.global;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthController.class)
public class HealthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void 테스트_컨트롤러_테스트() throws Exception {
		mockMvc.perform(get("/health-check"))
			.andExpect(status().isOk())
			.andExpect(content().string("OK"));
	}
}
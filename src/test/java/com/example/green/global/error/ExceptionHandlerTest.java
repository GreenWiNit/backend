package com.example.green.global.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.green.dummy.ErrorDummyController;
import com.example.green.global.error.exception.GlobalExceptionMessage;

class ExceptionHandlerTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new ErrorDummyController())
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

	@Test
	void 비즈니스_예외를_처리한다() throws Exception {
		mockMvc.perform(get("/business-exception"))
			.andExpect(status().isBadGateway())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value("에러 발생"));
	}

	@Test
	void endpoint가_없으면_no_resource_예외가_발생한다() throws Exception {
		// given
		GlobalExceptionMessage message = GlobalExceptionMessage.NO_RESOURCE_MESSAGE;

		// when & then
		mockMvc.perform(get("/no-endpoint"))
			.andExpect(status().is(message.getHttpStatus().value()))
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(message.getMessage()));
	}

	@Test
	void 알_수_없는_예외일_경우_Internal_server_에러가_발생한다() throws Exception {
		// given
		GlobalExceptionMessage message = GlobalExceptionMessage.INTERNAL_SERVER_ERROR_MESSAGE;

		// when & then
		mockMvc.perform(get("/internal-server-exception"))
			.andExpect(status().is(message.getHttpStatus().value()))
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(message.getMessage()));
	}
}

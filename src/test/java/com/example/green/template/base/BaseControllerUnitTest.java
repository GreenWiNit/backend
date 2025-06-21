package com.example.green.template.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.web.context.WebApplicationContext;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest
public class BaseControllerUnitTest {

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext) {
		RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
	}
}

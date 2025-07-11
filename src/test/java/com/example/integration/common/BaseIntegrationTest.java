package com.example.integration.common;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import com.example.green.GreenApplication;

@SpringBootTest(classes = {
	GreenApplication.class,
	BaseIntegrationTest.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ComponentScan(basePackages = "com.example.integration")
@ActiveProfiles("test")
public class BaseIntegrationTest {
}

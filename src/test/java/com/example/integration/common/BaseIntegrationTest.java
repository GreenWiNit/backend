package com.example.integration.common;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.example.green.GreenApplication;
import com.example.integration.config.MySqlTestContainerConfig;

@SpringBootTest(classes = {
	GreenApplication.class,
	BaseIntegrationTest.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = "com.example.integration")
@ActiveProfiles("test")
@Import(MySqlTestContainerConfig.class)
public class BaseIntegrationTest {
}

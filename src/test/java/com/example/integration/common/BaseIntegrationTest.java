package com.example.integration.common;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import com.example.green.GreenApplication;

@SpringBootTest(classes = {
	GreenApplication.class,
	BaseIntegrationTest.class
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = "com.example.integration")
public class BaseIntegrationTest {
}

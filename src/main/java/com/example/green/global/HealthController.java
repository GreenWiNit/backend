package com.example.green.global;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Hidden
public class HealthController {

	@GetMapping(value = "/health-check")
	public String check() {
		return "OK";
	}
}

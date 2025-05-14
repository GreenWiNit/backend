package com.example.green.global;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	@GetMapping(value = "/health-check")
	public String check() {
		return "OK";
	}
}

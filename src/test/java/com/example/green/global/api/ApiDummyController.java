package com.example.green.global.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiDummyController {

	@GetMapping("/api-response")
	public ApiResponse<String> getApi() {
		return ApiResponse.ok(() -> "성공", "OK");
	}

	@GetMapping("/no-content")
	public NoContent getNoContent() {
		return NoContent.ok(() -> "성공");
	}

}

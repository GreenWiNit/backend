package com.example.green.unit.global.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.global.api.ApiResponse;
import com.example.green.global.api.NoContent;

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

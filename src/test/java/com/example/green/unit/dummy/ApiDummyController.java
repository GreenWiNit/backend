package com.example.green.unit.dummy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.global.api.ApiResponse;
import com.example.green.global.api.NoContent;

import jakarta.validation.Valid;

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

	@GetMapping("/valid-model-attribute")
	public NoContent validateModelAttribute(@Valid @ModelAttribute ValidationDto dto) {
		return NoContent.ok(() -> "성공");
	}

	@PostMapping("/valid-request-body")
	public NoContent validateRequestBody(@Valid @RequestBody ValidationDto dto) {
		return NoContent.ok(() -> "성공");
	}

}

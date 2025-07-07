package com.example.green.domain.auth.controller;

import static com.example.green.domain.auth.controller.message.PhoneVerificationResponseMessage.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.auth.controller.docs.PhoneVerificationControllerDocs;
import com.example.green.domain.auth.controller.dto.PhoneVerificationRequest;
import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;
import com.example.green.domain.auth.service.PhoneVerificationService;
import com.example.green.domain.auth.service.result.PhoneVerificationResult;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/phone")
public class PhoneVerificationController implements PhoneVerificationControllerDocs {

	private final PhoneVerificationService phoneVerificationService;

	@PostMapping("/request")
	public ApiTemplate<PhoneVerificationResult> request(@RequestBody @Valid PhoneVerificationRequest dto) {
		PhoneNumber phoneNumber = PhoneNumber.of(dto.phoneNumber());
		PhoneVerificationResult result = phoneVerificationService.request(phoneNumber);
		return ApiTemplate.ok(PHONE_VERIFICATION_REQUEST_SUCCESS, result);
	}

	@PostMapping("/verify")
	public NoContent verify(@RequestBody @Valid PhoneVerificationRequest dto) {
		PhoneNumber phoneNumber = PhoneNumber.of(dto.phoneNumber());
		phoneVerificationService.verify(phoneNumber);
		return NoContent.ok(PHONE_VERIFICATION_SUCCESS);
	}
}

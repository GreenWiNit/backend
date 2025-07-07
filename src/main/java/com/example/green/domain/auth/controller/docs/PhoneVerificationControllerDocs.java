package com.example.green.domain.auth.controller.docs;

import com.example.green.domain.auth.controller.dto.PhoneVerificationRequest;
import com.example.green.domain.auth.service.result.PhoneVerificationResult;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "인증 API", description = "OAuth2 로그인, 회원가입, 토큰 관리 등 인증 관련 API")
public interface PhoneVerificationControllerDocs {

	@Operation(summary = "휴대전화 인증 요청", description = "휴대전화 번호에 대한 인증 코드 발급을 요청합니다.")
	@ApiResponse(responseCode = "200", description = "휴대전화 인증 요청에 성공했습니다.")
	ApiTemplate<PhoneVerificationResult> request(PhoneVerificationRequest dto);

	@Operation(summary = "휴대전화 인증 확인", description = "휴대전화 번호로 인증 내역을 확인합니다.")
	@ApiResponse(responseCode = "200", description = "휴대전화 인증이 완료되었습니다.")
	NoContent verify(PhoneVerificationRequest dto);
}

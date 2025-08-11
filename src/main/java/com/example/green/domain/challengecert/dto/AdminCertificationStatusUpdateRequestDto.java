package com.example.green.domain.challengecert.dto;

import com.example.green.domain.challenge.entity.certification.CertificationStatus;

import jakarta.validation.constraints.NotNull;

/**
 * 관리자 인증 상태 업데이트 요청 DTO
 */
public record AdminCertificationStatusUpdateRequestDto(
	@NotNull(message = "인증 상태는 필수값입니다.")
	CertificationStatus status
) {
}

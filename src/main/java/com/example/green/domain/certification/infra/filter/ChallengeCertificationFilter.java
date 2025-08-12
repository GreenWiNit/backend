package com.example.green.domain.certification.infra.filter;

import com.example.green.domain.certification.domain.CertificationStatus;
import com.example.green.global.api.page.PageSearchCondition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "챌린지 인증 목록 조회 필터")
public record ChallengeCertificationFilter(
	@Schema(description = "챌린지명 (nullable)", example = "30분")
	String challengeName,
	@Schema(description = "챌린지 그룹 코드 (nullable)", example = "T-20250810-001")
	String groupCode,
	@Schema(description = "사용자 식별자 (nullable)", example = "google 24124")
	String memberKey,
	@Schema(description = "챌린지 인증 상태 (nullable)",
		type = "string", allowableValues = {"pending", "approved", "rejected"})
	CertificationStatus status,
	@NotNull
	@Schema(description = "챌린지 타입 (not null)", allowableValues = {"P", "T"})
	String type,
	@Schema(description = "페이지 수 (nullable)")
	Integer page,
	@Schema(description = "페이지 크기 (nullable)", defaultValue = "20", example = "")
	Integer size
) implements PageSearchCondition {
}

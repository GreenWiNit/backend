package com.example.green.domain.certification.infra.filter;

import java.util.List;

import com.example.green.domain.certification.domain.CertificationStatus;
import com.example.green.global.api.page.PageSearchCondition;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
	@ArraySchema(
		schema = @Schema(
			type = "string",
			allowableValues = {"pending", "approved", "rejected"},
			description = "인증 상태"
		),
		minItems = 0,
		uniqueItems = true
	)
	@Parameter(
		description = "챌린지 인증 상태 (다중 선택 가능)",
		explode = Explode.TRUE,
		style = ParameterStyle.FORM
	)
	List<CertificationStatus> status,
	@NotNull
	@Schema(description = "챌린지 타입 (not null)", allowableValues = {"P", "T"})
	String type,
	@Schema(description = "페이지 수 (nullable)")
	Integer page,
	@Schema(description = "페이지 크기 (nullable)", defaultValue = "20", example = "")
	Integer size
) implements PageSearchCondition {
}

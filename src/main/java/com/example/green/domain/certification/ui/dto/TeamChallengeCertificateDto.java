package com.example.green.domain.certification.ui.dto;

import com.example.green.domain.certification.application.command.TeamChallengeCertificateCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "팀 챌린지 인증")
public record TeamChallengeCertificateDto(
	@NotBlank
	@Schema(description = "챌린지 인증 이미지", example = "https://example.com/image.png")
	String imageUrl,
	@NotBlank
	@Schema(description = "챌린지 인증 후기", example = "아 좋았습니다.")
	String review
) {

	public TeamChallengeCertificateCommand toCommand(Long memberId, Long groupId) {
		return TeamChallengeCertificateCommand.of(memberId, groupId, imageUrl, review);
	}
}

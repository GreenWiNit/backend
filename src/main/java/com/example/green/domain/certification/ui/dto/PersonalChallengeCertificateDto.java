package com.example.green.domain.certification.ui.dto;

import java.time.LocalDate;

import com.example.green.domain.certification.application.command.PersonalChallengeCertificateCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "팀 챌린지 인증")
public record PersonalChallengeCertificateDto(
	@NotNull
	@Schema(description = "챌린지 인증 날짜")
	LocalDate challengeDate,
	@NotBlank
	@Schema(description = "챌린지 인증 이미지", example = "https://example.com/image.png")
	String imageUrl,
	@NotBlank
	@Schema(description = "챌린지 인증 후기", example = "아 좋았습니다.")
	String review
) {

	public PersonalChallengeCertificateCommand toCommand(Long memberId, Long challengeId) {
		return PersonalChallengeCertificateCommand.of(memberId, challengeId, challengeDate, imageUrl, review);
	}
}

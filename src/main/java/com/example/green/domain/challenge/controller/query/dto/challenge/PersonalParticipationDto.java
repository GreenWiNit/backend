package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "개인 챌린지 참여 목록 응답")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonalParticipationDto {
	@Schema(description = "사용자 식별 키", example = "google 12421424")
	private String memberKey;
	@Schema(description = "챌린지 참여 일자", example = "2025-08-10")
	private LocalDateTime participatingDate;
	@Schema(description = "인증 회수", example = "1")
	private Integer certCount;
	@JsonIgnore
	Long memberId;

	public PersonalParticipationDto(Long memberId, LocalDateTime participatingDate, Integer certCount) {
		this.memberId = memberId;
		this.participatingDate = participatingDate;
		this.certCount = certCount;
	}

	public LocalDate getParticipatingDate() {
		return participatingDate.toLocalDate();
	}
}

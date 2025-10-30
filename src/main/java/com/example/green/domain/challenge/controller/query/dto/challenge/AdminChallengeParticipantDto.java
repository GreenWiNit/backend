package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.controller.query.dto.MemberKeySettable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "어드민 챌린지 참가자 목록 응답")
@Data
@NoArgsConstructor
public class AdminChallengeParticipantDto implements MemberKeySettable {
	@Schema(description = "팀 코드", example = "T-20250109-143523-C8NQ")
	private String teamCode;

	@Schema(description = "사용자 식별 키", example = "google 12421424")
	private String memberKey;

	@Schema(description = "챌린지 참여 날짜", example = "2025-08-10")
	private LocalDateTime participatingDate;

	@Schema(description = "팀 참여 일자", example = "2025-08-10")
	private LocalDateTime teamParticipatingDate;

	@Schema(description = "인증 횟수", example = "1")
	private Integer certCount;

	@JsonIgnore
	private Long memberId;

	public AdminChallengeParticipantDto(Long memberId, LocalDateTime participatingDate, Integer certCount) {
		this.memberId = memberId;
		this.participatingDate = participatingDate;
		this.certCount = certCount;
	}

	public AdminChallengeParticipantDto(
		String teamCode, Long memberId, LocalDateTime participatingDate, LocalDateTime teamParticipatingDate
	) {
		this.teamCode = teamCode;
		this.memberId = memberId;
		this.participatingDate = participatingDate;
		this.teamParticipatingDate = teamParticipatingDate;
	}
}

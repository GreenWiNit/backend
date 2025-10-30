package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "어드민 챌린지 목록 응답")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminChallengesDto {
	@Schema(description = "챌린지 ID", example = "1")
	private Long id;

	@Schema(description = "챌린지 코드", example = "CH-T-20250109-143522-B7MX")
	private String code;

	@Schema(description = "챌린지명", example = "30일 운동 챌린지")
	private String name;

	@Schema(description = "챌린지 포인트", example = "100")
	private Integer point;

	@Schema(description = "팀 수", example = "4")
	private Long teamCount;

	@Schema(description = "전시 상태", example = "VISIBLE")
	private ChallengeDisplay display;

	@Schema(description = "생성 일시")
	private LocalDateTime createdDate;
}

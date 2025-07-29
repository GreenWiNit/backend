package com.example.green.domain.challengecert.dto;

import java.util.List;

import com.example.green.domain.challengecert.enums.CertificationStatus;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 관리자 팀 챌린지 인증 목록 검색 요청 DTO
 * 페이지 크기는 10건으로 고정되어 있습니다.
 */
@Schema(description = "관리자 팀 챌린지 인증 목록 검색 조건 (10건씩 조회)")
public record AdminTeamCertificationSearchRequestDto(
	@Schema(description = "챌린지 ID (선택사항, null이면 전체 조회)", example = "1")
	Long challengeId,

	@Schema(description = "그룹 코드 (선택사항, null이면 전체 그룹)", example = "T-20250109-143523-C8NQ")
	String groupCode,

	@Schema(description = "인증 상태 리스트 (선택사항, null이면 전체 상태)",
		example = "[\"PENDING\", \"PAID\", \"REJECTED\"]")
	List<CertificationStatus> statuses,

	@Schema(description = "커서 (페이징용, 마지막 인증 ID)")
	Long cursor
) {

	/**
	 * 전체 챌린지 조회 여부
	 */
	public boolean isAllChallenges() {
		return challengeId == null;
	}

	/**
	 * 전체 그룹 조회 여부
	 */
	public boolean isAllGroups() {
		return groupCode == null || groupCode.trim().isEmpty();
	}

	/**
	 * 전체 상태 조회 여부
	 */
	public boolean isAllStatuses() {
		return statuses == null || statuses.isEmpty();
	}
}

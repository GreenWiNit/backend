package com.example.green.domain.challengecert.dto;

import java.util.List;

import com.example.green.domain.challengecert.enums.CertificationStatus;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 관리자 개인 챌린지 인증 목록 검색 요청 DTO
 */
@Schema(description = "관리자 개인 챌린지 인증 목록 검색 조건")
public record AdminPersonalCertificationSearchRequestDto(
	@Schema(description = "챌린지 ID (선택사항, null이면 전체 조회)", example = "1")
	Long challengeId,

	@Schema(description = "참여자 memberKey (선택사항, null이면 전체 참여자)", example = "google_3421")
	String memberKey,

	@Schema(description = "인증 상태 리스트 (선택사항, null이면 전체 상태)",
		example = "[\"PENDING\", \"PAID\", \"REJECTED\"]")
	List<CertificationStatus> statuses,

	@Schema(description = "커서 (페이징용, 마지막 인증 ID)")
	Long cursor,

	@Schema(description = "페이지 크기 (기본값: 10, 백엔드에서 고정)", example = "10", hidden = true)
	Integer size
) {
	public AdminPersonalCertificationSearchRequestDto {
		// 백엔드에서 고정값 사용 (프론트에서 받지 않음)
		size = 10;
	}

	/**
	 * 전체 챌린지 조회 여부
	 */
	public boolean isAllChallenges() {
		return challengeId == null;
	}

	/**
	 * 전체 참여자 조회 여부
	 */
	public boolean isAllMembers() {
		return memberKey == null || memberKey.trim().isEmpty();
	}

	/**
	 * 전체 상태 조회 여부
	 */
	public boolean isAllStatuses() {
		return statuses == null || statuses.isEmpty();
	}
}

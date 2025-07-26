package com.example.green.domain.challenge.repository;

import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupListResponseDto;
import com.example.green.global.api.page.CursorTemplate;

/**
 * 팀 챌린지 그룹 조회를 위한 커스텀 리포지토리
 * QueryDSL을 사용하여 커서 기반 페이지네이션을 구현합니다.
 */
public interface TeamChallengeGroupRepositoryCustom {

	/**
	 * 특정 팀 챌린지의 그룹 목록을 커서 기반으로 조회합니다.
	 * @param challengeId 팀 챌린지 ID
	 * @param cursor 마지막으로 조회한 그룹의 ID
	 * @param size 조회할 그룹 수
	 * @param memberId 현재 사용자 ID (리더 여부 확인용)
	 * @return 커서 템플릿에 담긴 팀 챌린지 그룹 목록
	 */
	CursorTemplate<Long, TeamChallengeGroupListResponseDto> findGroupsByChallengeIdAndCursor(
		Long challengeId,
		Long cursor,
		int size,
		Long memberId
	);

	CursorTemplate<Long, AdminTeamChallengeGroupListResponseDto> findAllForAdminByCursor(Long cursor, int size);
}

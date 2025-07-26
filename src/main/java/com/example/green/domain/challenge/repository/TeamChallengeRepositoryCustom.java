package com.example.green.domain.challenge.repository;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeListResponseDto;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.global.api.page.CursorTemplate;

/**
 * 팀 챌린지 조회를 위한 커스텀 리포지토리
 * QueryDSL을 사용하여 커서 기반 페이지네이션을 구현합니다.
 */
public interface TeamChallengeRepositoryCustom {

	/**
	 * 팀 챌린지 목록을 커서 기반으로 조회합니다.
	 * @param cursor 마지막으로 조회한 챌린지의 ID
	 * @param size 조회할 챌린지 수
	 * @param status 챌린지 상태
	 * @param now 현재 시간
	 * @return 커서 템플릿에 담긴 팀 챌린지 목록
	 */
	CursorTemplate<Long, ChallengeListResponseDto> findTeamChallengesByCursor(
		Long cursor,
		int size,
		ChallengeStatus status,
		LocalDateTime now
	);

	CursorTemplate<Long, AdminTeamChallengeListResponseDto> findAllForAdminByCursor(Long cursor, int size);
}

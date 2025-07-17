package com.example.green.domain.challengecert.repository;

import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.global.api.page.CursorTemplate;

/**
 * 개인 챌린지 참여 정보 조회를 위한 커스텀 레포지토리
 * QueryDSL을 사용하여 커서 기반 페이지네이션을 구현합니다.
 */
public interface PersonalChallengeParticipationRepositoryCustom {

	/**
	 * 회원의 개인 챌린지 참여 목록을 커서 기반으로 조회합니다.
	 * @param member 회원 정보
	 * @param cursor 마지막으로 조회한 참여 정보의 ID
	 * @param size 조회할 참여 정보 수
	 * @return 커서 템플릿에 담긴 챌린지 목록
	 */
	CursorTemplate<Long, ChallengeListResponseDto> findMyParticipationsByCursor(
		Member member,
		Long cursor,
		int size
	);
}

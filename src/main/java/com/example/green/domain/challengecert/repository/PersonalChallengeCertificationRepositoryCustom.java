package com.example.green.domain.challengecert.repository;

import java.util.Optional;

import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.domain.challengecert.entity.PersonalChallengeCertification;
import com.example.green.domain.member.entity.Member;
import com.example.green.global.api.page.CursorTemplate;

/**
 * 개인 챌린지 인증 조회를 위한 커스텀 리포지토리
 * QueryDSL을 사용하여 커서 기반 페이지네이션을 구현합니다.
 */
public interface PersonalChallengeCertificationRepositoryCustom {

	/**
	 * 특정 회원의 개인 챌린지 인증 목록을 커서 기반으로 조회합니다.
	 * @param member 조회할 회원
	 * @param cursor 마지막으로 조회한 인증의 ID
	 * @param size 조회할 인증 수
	 * @return 커서 템플릿에 담긴 개인 챌린지 인증 목록
	 */
	CursorTemplate<Long, ChallengeCertificationListResponseDto> findByMemberWithCursor(
		Member member,
		Long cursor,
		int size
	);

	/**
	 * 특정 ID와 회원으로 개인 챌린지 인증을 조회합니다.
	 * @param id 인증 ID
	 * @param member 회원
	 * @return 개인 챌린지 인증 정보
	 */
	Optional<PersonalChallengeCertification> findByIdAndMember(Long id, Member member);
}

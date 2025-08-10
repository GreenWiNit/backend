/*
package com.example.green.domain.challengecert.repository;

import java.util.List;
import java.util.Optional;

import com.example.green.domain.challenge.entity.certification.TeamChallengeCertification;
import com.example.green.domain.challengecert.dto.AdminGroupCodeResponseDto;
import com.example.green.domain.challengecert.dto.AdminTeamCertificationSearchRequestDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.global.api.page.CursorTemplate;

*/
/**
 * 팀 챌린지 인증 조회를 위한 커스텀 리포지토리
 * QueryDSL을 사용하여 커서 기반 페이지네이션을 구현합니다.
 *//*

public interface TeamChallengeCertificationRepositoryCustom {

	*/
/**
 * 특정 회원의 팀 챌린지 인증 목록을 커서 기반으로 조회합니다.
 * @param member 조회할 회원
 * @param cursor 마지막으로 조회한 인증의 ID
 * @param size 조회할 인증 수
 * @return 커서 템플릿에 담긴 팀 챌린지 인증 목록
 *//*

	CursorTemplate<Long, ChallengeCertificationListResponseDto> findByMemberWithCursor(
		Member member,
		Long cursor,
		int size
	);

	*/
/**
 * 특정 ID와 회원으로 팀 챌린지 인증을 조회합니다.
 * @param id 인증 ID
 * @param member 회원
 * @return 팀 챌린지 인증 정보
 *//*

	Optional<TeamChallengeCertification> findByIdAndMember(Long id, Member member);

	*/
/**
 * 팀 챌린지의 그룹 코드 목록을 조회합니다. (관리자용)
 * @param challengeId 챌린지 ID
 * @return 그룹 코드 목록
 *//*

	List<AdminGroupCodeResponseDto> findGroupCodesByChallengeId(Long challengeId);

	*/
/**
 * 관리자용 팀 챌린지 인증 목록을 복합 조건으로 조회합니다. (커서 기반 페이징)
 * @param searchRequest 검색 조건
 * @param pageSize 페이지 크기
 * @return 커서 템플릿에 담긴 팀 챌린지 인증 목록
 *//*

	CursorTemplate<Long, ChallengeCertificationListResponseDto> findTeamCertificationsWithFilters(
		AdminTeamCertificationSearchRequestDto searchRequest,
		int pageSize
	);
}
*/

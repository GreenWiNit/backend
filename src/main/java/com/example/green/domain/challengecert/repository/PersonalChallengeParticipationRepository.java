package com.example.green.domain.challengecert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;
import com.example.green.domain.member.entity.Member;

/**
 * 개인 챌린지 참여 정보를 관리하는 레포지토리
 */
public interface PersonalChallengeParticipationRepository
	extends JpaRepository<PersonalChallengeParticipation, Long>, PersonalChallengeParticipationRepositoryCustom {

	/**
	 * 회원의 개인 챌린지 참여 여부를 확인합니다.
	 */
	boolean existsByMemberAndPersonalChallenge(Member member, PersonalChallenge challenge);

	/**
	 * 회원의 개인 챌린지 참여 정보를 조회합니다.
	 */
	Optional<PersonalChallengeParticipation> findByMemberAndPersonalChallenge(Member member,
		PersonalChallenge challenge);

	/**
	 * 회원의 개인 챌린지 참여 목록을 커서 기반으로 조회합니다.
	 */
	@Query("SELECT p FROM PersonalChallengeParticipation p " +
		"WHERE p.member = :member " +
		"AND (:cursor IS NULL OR p.id < :cursor) " +
		"ORDER BY p.id DESC " +
		"LIMIT :limit")
	List<PersonalChallengeParticipation> findMyParticipationsByCursor(
		@Param("member") Member member,
		@Param("cursor") Long cursor,
		@Param("limit") int limit
	);

	/**
	 * 다음 개인 챌린지 참여 정보가 존재하는지 확인합니다.
	 */
	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PersonalChallengeParticipation p " +
		"WHERE p.member = :member " +
		"AND p.id < :cursor")
	boolean existsNextParticipation(
		@Param("member") Member member,
		@Param("cursor") Long cursor
	);
}

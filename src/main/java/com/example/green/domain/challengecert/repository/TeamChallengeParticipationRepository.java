package com.example.green.domain.challengecert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.member.entity.Member;

/**
 * 팀 챌린지 참여 정보를 관리하는 레포지토리
 */
public interface TeamChallengeParticipationRepository extends JpaRepository<TeamChallengeParticipation, Long>, TeamChallengeParticipationRepositoryCustom {

    /**
     * 회원의 팀 챌린지 참여 여부를 확인합니다.
     */
    boolean existsByMemberAndTeamChallenge(Member member, TeamChallenge challenge);

    /**
     * 회원의 팀 챌린지 참여 정보를 조회합니다.
     */
    Optional<TeamChallengeParticipation> findByMemberAndTeamChallenge(Member member, TeamChallenge challenge);

    /**
     * 회원의 팀 챌린지 참여 목록을 커서 기반으로 조회합니다.
     */
    @Query("SELECT t FROM TeamChallengeParticipation t " +
        "WHERE t.member = :member " +
        "AND (:cursor IS NULL OR t.id < :cursor) " +
        "ORDER BY t.id DESC " +
        "LIMIT :limit")
    List<TeamChallengeParticipation> findMyParticipationsByCursor(
        @Param("member") Member member,
        @Param("cursor") Long cursor,
        @Param("limit") int limit
    );

    /**
     * 다음 팀 챌린지 참여 정보가 존재하는지 확인합니다.
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TeamChallengeParticipation t " +
        "WHERE t.member = :member " +
        "AND t.id < :cursor")
    boolean existsNextParticipation(
        @Param("member") Member member,
        @Param("cursor") Long cursor
    );
} 
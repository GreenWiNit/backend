package com.example.green.domain.challengecert.repository;

import java.util.List;

import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;
import com.example.green.domain.member.entity.Member;

/**
 * 개인 챌린지 참여 정보 조회를 위한 커스텀 레포지토리
 * 커서 기반 페이지네이션을 사용하여 챌린지 참여 목록을 조회합니다.
 */
public interface PersonalChallengeParticipationRepositoryCustom {

    /**
     * 회원의 개인 챌린지 참여 목록을 커서 기반으로 조회합니다.
     * @param member 회원 정보
     * @param cursor 마지막으로 조회한 참여 정보의 ID
     * @param size 조회할 참여 정보 수
     * @return 조회된 개인 챌린지 참여 목록
     */
    List<PersonalChallengeParticipation> findMyParticipationsByCursor(
        Member member,
        Long cursor,
        int size
    );

    /**
     * 다음 개인 챌린지 참여 정보가 존재하는지 확인합니다.
     * @param member 회원 정보
     * @param cursor 현재 커서
     * @return 다음 참여 정보 존재 여부
     */
    boolean existsNextParticipation(
        Member member,
        Long cursor
    );
} 
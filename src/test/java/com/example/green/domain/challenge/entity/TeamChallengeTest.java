package com.example.green.domain.challenge.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.utils.ChallengeCodeGenerator;
import com.example.green.domain.point.entity.vo.PointAmount;

class TeamChallengeTest {

    private TeamChallenge teamChallenge;
    private PointAmount challengePoint;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        challengePoint = PointAmount.of(BigDecimal.valueOf(2000));
        
        teamChallenge = TeamChallenge.create(
            ChallengeCodeGenerator.generate(ChallengeType.TEAM, now),
            "팀 챌린지",
            ChallengeStatus.PROCEEDING,
            challengePoint,
            now.minusDays(1),
            now.plusDays(7),
            5,  // 최대 5팀
            "challenge-image.jpg",
            "팀 챌린지 설명"
        );
    }

    @Test
    void TeamChallenge는_TEAM_타입을_가진다() {
        // when
        ChallengeType type = teamChallenge.getChallengeType();

        // then
        assertThat(type).isEqualTo(ChallengeType.TEAM);
    }

    @Test
    void TeamChallenge_생성_시_challengeCode가_자동으로_생성된다() {
        // when
        String challengeCode = teamChallenge.getChallengeCode();

        // then
        assertThat(challengeCode).isNotNull();
        assertThat(challengeCode).startsWith("CH-T-");
        assertThat(challengeCode).hasSize(25); // CH-T-20250109-143521-A3FV 형태 (25자)
        // 날짜, 시간, ULID 뒷 4자리 형식 확인
        assertThat(challengeCode).matches("CH-T-\\d{8}-\\d{6}-[0-9A-HJKMNP-TV-Z]{4}");
    }

    @Test
    void 각_TeamChallenge마다_고유한_challengeCode를_가진다() throws InterruptedException {
        // given
        LocalDateTime testNow1 = now;
        Thread.sleep(1); // 시간 차이를 만들기 위해
        LocalDateTime testNow2 = LocalDateTime.now();
        
        TeamChallenge challenge1 = TeamChallenge.create(
            ChallengeCodeGenerator.generate(ChallengeType.TEAM, testNow1),
            "첫 번째 팀 챌린지",
            ChallengeStatus.PROCEEDING,
            challengePoint,
            testNow1.minusDays(1),
            testNow1.plusDays(7),
            5,
            "challenge-image.jpg",
            "첫 번째 팀 챌린지 설명"
        );

        TeamChallenge challenge2 = TeamChallenge.create(
            ChallengeCodeGenerator.generate(ChallengeType.TEAM, testNow2),
            "두 번째 팀 챌린지",
            ChallengeStatus.PROCEEDING,
            challengePoint,
            testNow2.minusDays(1),
            testNow2.plusDays(7),
            3,
            "challenge-image.jpg",
            "두 번째 팀 챌린지 설명"
        );

        // when
        String code1 = challenge1.getChallengeCode();
        String code2 = challenge2.getChallengeCode();

        // then
        assertThat(code1).isNotEqualTo(code2);
        assertThat(code1).startsWith("CH-T-");
        assertThat(code2).startsWith("CH-T-");
    }

    @Test
    void 팀_챌린지_코드가_시간_기반으로_올바르게_생성된다() {
        // given
        LocalDateTime testNow = now;

        // when
        TeamChallenge challenge = TeamChallenge.create(
            ChallengeCodeGenerator.generate(ChallengeType.TEAM, testNow),
            "시간 기반 팀 챌린지",
            ChallengeStatus.PROCEEDING,
            challengePoint,
            testNow,
            testNow.plusDays(7),
            10,
            "challenge-image.jpg",
            "시간 기반 팀 챌린지 설명"
        );

        // then
        String challengeCode = challenge.getChallengeCode();
        String today = testNow.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 형식 검증: CH-T-yyyyMMdd-HHmmss-XXXX (날짜-시간-ULID 뒷 4자리)
        assertThat(challengeCode).matches("CH-T-\\d{8}-\\d{6}-[0-9A-HJKMNP-TV-Z]{4}");
        assertThat(challengeCode).contains(today); // 오늘 날짜 포함
        assertThat(challengeCode).hasSize(25);

        // 타입 확인
        assertThat(challengeCode).startsWith("CH-T-");

        // ULID 뒷 4자리 부분이 Base32 문자인지 확인
        String ulidPart = challengeCode.substring(challengeCode.length() - 4);
        assertThat(ulidPart).matches("[0-9A-HJKMNP-TV-Z]{4}");
    }

    @Test
    void 그룹_추가_시_챌린지_그룹_리스트에_추가되고_팀_카운트가_증가한다() {
        // given & when
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "테스트 그룹 1",
            testNow.plusDays(1),
            testNow.plusDays(8),
            10,
            "서울시 강남구",
            "테스트 그룹 설명",
            "https://openchat.example.com",
            teamChallenge
        );

        // then
        assertThat(teamChallenge.getChallengeGroups()).contains(group);
        assertThat(teamChallenge.getCurrentGroupCount()).isEqualTo(1);
        assertThat(group.getTeamChallenge()).isEqualTo(teamChallenge);
    }

    @Test
    void 여러_그룹_추가_시_팀_카운트가_올바르게_증가한다() {
        // given & when
        LocalDateTime testNow = now;
        TeamChallengeGroup group1 = TeamChallengeGroup.create(
            "테스트 그룹 1",
            testNow.plusDays(1),
            testNow.plusDays(8),
            10,
            null, null, null,
            teamChallenge
        );
        TeamChallengeGroup group2 = TeamChallengeGroup.create(
            "테스트 그룹 2",
            testNow.plusDays(1),
            testNow.plusDays(8),
            10,
            null, null, null,
            teamChallenge
        );
        TeamChallengeGroup group3 = TeamChallengeGroup.create(
            "테스트 그룹 3",
            testNow.plusDays(1),
            testNow.plusDays(8),
            10,
            null, null, null,
            teamChallenge
        );

        // then
        assertThat(teamChallenge.getCurrentGroupCount()).isEqualTo(3);
        assertThat(teamChallenge.getChallengeGroups()).hasSize(3);
    }

    @Test
    void 그룹_제거_시_챌린지_그룹_리스트에서_제거되고_팀_카운트가_감소한다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "테스트 그룹",
            testNow.plusDays(1),
            testNow.plusDays(8),
            10,
            null, null, null,
            teamChallenge
        );
        // TeamChallengeGroup.create()에서 이미 addChallengeGroup()이 호출됨
        
        // when
        teamChallenge.removeChallengeGroup(group);

        // then
        assertThat(teamChallenge.getChallengeGroups()).doesNotContain(group);
        assertThat(teamChallenge.getCurrentGroupCount()).isEqualTo(0);
        assertThat(group.getTeamChallenge()).isNull();
    }

    @Test
    void maxTeamCount가_null이면_항상_팀_추가_가능하다() {
        // given
        TeamChallenge challenge = TeamChallenge.create(
            ChallengeCodeGenerator.generate(ChallengeType.TEAM, LocalDateTime.now()),
            "무제한 챌린지",
            ChallengeStatus.PROCEEDING,
            challengePoint,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(7),
            null,  // 제한 없음
            "challenge-image.jpg",
            "무제한 챌린지 설명"
        );

        // when
        boolean canAddTeam = challenge.canAddGroup();

        // then
        assertTrue(canAddTeam);
    }

    @Test
    void 현재_팀_수가_최대_팀_수보다_적으면_팀_추가_가능하다() {
        // given (현재 0팀, 최대 5팀)

        // when
        boolean canAddTeam = teamChallenge.canAddGroup();

        // then
        assertTrue(canAddTeam);
    }

    @Test
    void 현재_팀_수가_최대_팀_수와_같으면_팀_추가_불가능하다() {
        // given - 3개 그룹을 미리 추가
        TeamChallenge challenge = TeamChallenge.create(
            ChallengeCodeGenerator.generate(ChallengeType.TEAM, LocalDateTime.now()),
            "만석 챌린지",
            ChallengeStatus.PROCEEDING,
            challengePoint,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(7),
            3,  // 최대 3팀
            "challenge-image.jpg",
            "만석 챌린지 설명"
        );

        // 3개 그룹을 추가하여 만석 상태로 만듦
        for (int i = 0; i < 3; i++) {
            TeamChallengeGroup group = TeamChallengeGroup.create(
                "그룹 " + (i + 1),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(8),
                10,
                null, null, null,
                challenge
            );
        }

        // when
        boolean canAddTeam = challenge.canAddGroup();

        // then
        assertFalse(canAddTeam);
    }

    @Test
    void 최대_팀_수에_도달했는지_올바르게_확인한다() {
        // given
        TeamChallenge challenge = TeamChallenge.create(
            ChallengeCodeGenerator.generate(ChallengeType.TEAM, LocalDateTime.now()),
            "만석 챌린지",
            ChallengeStatus.PROCEEDING,
            challengePoint,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(7),
            2,  // 최대 2팀
            "challenge-image.jpg",
            "만석 챌린지 설명"
        );

        // 2개 그룹을 추가하여 만석 상태로 만듦
        for (int i = 0; i < 2; i++) {
            TeamChallengeGroup group = TeamChallengeGroup.create(
                "그룹 " + (i + 1),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(8),
                10,
                null, null, null,
                challenge
            );
        }

        // when
        boolean isMaxReached = challenge.isMaxGroupCountReached();

        // then
        assertTrue(isMaxReached);
    }

    @Test
    void maxTeamCount가_null이면_최대_팀_수에_도달하지_않은_것으로_판단한다() {
        // given
        TeamChallenge challenge = TeamChallenge.create(
            ChallengeCodeGenerator.generate(ChallengeType.TEAM, LocalDateTime.now()),
            "무제한 챌린지",
            ChallengeStatus.PROCEEDING,
            challengePoint,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(7),
            null,  // 제한 없음
            "challenge-image.jpg",
            "무제한 챌린지 설명"
        );

        // when
        boolean isMaxReached = challenge.isMaxGroupCountReached();

        // then
        assertFalse(isMaxReached);
    }

    @Test
    void 최대_그룹_수가_0_이하이면_ChallengeException이_발생한다() {
        // when & then
        assertThatThrownBy(() -> TeamChallenge.create(
            ChallengeCodeGenerator.generate(ChallengeType.TEAM, LocalDateTime.now()),
            "잘못된 챌린지",
            ChallengeStatus.PROCEEDING,
            challengePoint,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(7),
            0,  // 잘못된 값
            "challenge-image.jpg",
            "잘못된 챌린지 설명"
        ))
            .isInstanceOf(ChallengeException.class)
            .hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.INVALID_MAX_GROUP_COUNT);
    }

    @Test
    void 최대_그룹_수가_음수이면_ChallengeException이_발생한다() {
        // when & then
        assertThatThrownBy(() -> TeamChallenge.create(
            ChallengeCodeGenerator.generate(ChallengeType.TEAM, LocalDateTime.now()),
            "잘못된 챌린지",
            ChallengeStatus.PROCEEDING,
            challengePoint,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(7),
            -5,  // 잘못된 값
            "challenge-image.jpg",
            "잘못된 챌린지 설명"
        ))
            .isInstanceOf(ChallengeException.class)
            .hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.INVALID_MAX_GROUP_COUNT);
    }
}

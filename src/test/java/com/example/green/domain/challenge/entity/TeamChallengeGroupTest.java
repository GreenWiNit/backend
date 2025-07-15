package com.example.green.domain.challenge.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.enums.GroupStatus;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;

class TeamChallengeGroupTest {

    private TeamChallengeGroup teamChallengeGroup;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        teamChallengeGroup = TeamChallengeGroup.create(
            "테스트 그룹",
            now.minusHours(1),  // 1시간 전 시작
            now.plusHours(1),   // 1시간 후 종료
            10,  // 최대 10명
            "서울시 강남구",
            "테스트 그룹 설명",
            "https://openchat.example.com"
        );

        // 현재 참가자 수를 5명으로 설정
        for (int i = 0; i < 5; i++) {
            teamChallengeGroup.increaseParticipants();
        }
    }

    @Test
    void 참가자_증가_시_currentParticipants가_1_증가한다() {
        // given
        int initialCount = teamChallengeGroup.getCurrentParticipants();

        // when
        teamChallengeGroup.increaseParticipants();

        // then
        assertThat(teamChallengeGroup.getCurrentParticipants()).isEqualTo(initialCount + 1);
    }

    @Test
    void 참가자_감소_시_currentParticipants가_1_감소한다() {
        // given
        int initialCount = teamChallengeGroup.getCurrentParticipants();

        // when
        teamChallengeGroup.decreaseParticipants();

        // then
        assertThat(teamChallengeGroup.getCurrentParticipants()).isEqualTo(initialCount - 1);
    }

    @Test
    void currentParticipants가_0일_때_참가자_감소해도_0을_유지한다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "테스트 그룹",
            testNow.minusHours(1),
            testNow.plusHours(1),
            10,
            null, null, null
        );
        // currentParticipants는 생성 시 0으로 초기화됨

        // when
        group.decreaseParticipants();

        // then
        assertThat(group.getCurrentParticipants()).isEqualTo(0);
    }

    @Test
    void 최대_인원에_도달하면_true를_반환한다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "만석 그룹",
            testNow.minusHours(1),
            testNow.plusHours(1),
            5,   // 최대 5명
            null, null, null
        );

        // 5명을 추가하여 만석으로 만듦
        for (int i = 0; i < 5; i++) {
            group.increaseParticipants();
        }

        // when
        boolean isMaxReached = group.isMaxParticipantsReached();

        // then
        assertTrue(isMaxReached);
    }

    @Test
    void maxParticipants가_null이면_최대_인원에_도달하지_않은_것으로_판단한다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "무제한 그룹",
            testNow.minusHours(1),
            testNow.plusHours(1),
            null, // 제한 없음
            null, null, null
        );

        // 100명을 추가
        for (int i = 0; i < 100; i++) {
            group.increaseParticipants();
        }

        // when
        boolean isMaxReached = group.isMaxParticipantsReached();

        // then
        assertFalse(isMaxReached);
    }

    @Test
    void 최대인원_미달이고_종료시간_전이면_참가_가능하다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "참가 가능한 그룹",
            testNow.minusHours(1),
            testNow.plusHours(1),  // 종료시간 전
            10,  // 최대 10명
            null, null, null
        );

        // 5명만 추가 (여유 있음)
        for (int i = 0; i < 5; i++) {
            group.increaseParticipants();
        }

        // when
        boolean canParticipate = group.canParticipate(testNow);

        // then
        assertTrue(canParticipate);
    }

    @Test
    void 최대_인원에_도달하면_참가할_수_없다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "만석 그룹",
            testNow.minusHours(1),
            testNow.plusHours(1),
            5,   // 최대 5명
            null, null, null
        );

        // 5명을 추가하여 만석으로 만듦
        for (int i = 0; i < 5; i++) {
            group.increaseParticipants();
        }

        // when
        boolean canParticipate = group.canParticipate(testNow);

        // then
        assertFalse(canParticipate);
    }

    @Test
    void 종료_시간이_지나면_참가할_수_없다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "종료된 그룹",
            testNow.minusHours(2),
            testNow.minusHours(1),  // 1시간 전 종료
            10,
            null, null, null
        );

        // when
        boolean canParticipate = group.canParticipate(testNow);

        // then
        assertFalse(canParticipate);
    }

    @Test
    void 현재_시간이_그룹_기간_내에_있으면_활성_상태이다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "활성 그룹",
            testNow.minusHours(1),  // 1시간 전 시작
            testNow.plusHours(1),   // 1시간 후 종료
            10,
            null, null, null
        );

        // when
        boolean isActive = group.isActive(testNow);

        // then
        assertTrue(isActive);
    }

    @Test
    void 시작_시간_이전이면_활성_상태가_아니다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "미래 그룹",
            testNow.plusHours(1),  // 1시간 후 시작
            testNow.plusHours(2),
            10,
            null, null, null
        );

        // when
        boolean isActive = group.isActive(testNow);

        // then
        assertFalse(isActive);
    }

    @Test
    void 종료_시간_이후면_활성_상태가_아니다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "종료된 그룹",
            testNow.minusHours(2),
            testNow.minusHours(1),  // 1시간 전 종료
            10,
            null, null, null
        );

        // when
        boolean isActive = group.isActive(testNow);

        // then
        assertFalse(isActive);
    }

    @Test
    void 최대_참가자_수가_0_이하이면_ChallengeException이_발생한다() {
        // when & then
        assertThatThrownBy(() -> TeamChallengeGroup.create(
            "잘못된 그룹",
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusHours(1),
            0,  // 잘못된 값
            null, null, null
        ))
            .isInstanceOf(ChallengeException.class)
            .hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.INVALID_MAX_PARTICIPANTS_COUNT);
    }

    @Test
    void 최대_참가자_수가_음수이면_ChallengeException이_발생한다() {
        // when & then
        assertThatThrownBy(() -> TeamChallengeGroup.create(
            "잘못된 그룹",
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusHours(1),
            -10,  // 잘못된 값
            null, null, null
        ))
            .isInstanceOf(ChallengeException.class)
            .hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.INVALID_MAX_PARTICIPANTS_COUNT);
    }

    @Test
    void 최대_인원_도달_시_상태가_COMPLETED로_변경된다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "테스트 그룹",
            testNow.minusHours(1),
            testNow.plusHours(1),
            5,   // 최대 5명
            null, null, null
        );

        // when
        // 5명을 추가하여 만석으로 만듦
        for (int i = 0; i < 5; i++) {
            group.increaseParticipants();
        }

        // then
        assertThat(group.getGroupStatus()).isEqualTo(GroupStatus.COMPLETED);
    }

    @Test
    void 최대_인원_미달_시_상태가_RECRUITING으로_유지된다() {
        // given
        LocalDateTime testNow = now;
        TeamChallengeGroup group = TeamChallengeGroup.create(
            "테스트 그룹",
            testNow.minusHours(1),
            testNow.plusHours(1),
            10,   // 최대 10명
            null, null, null
        );

        // when
        // 5명만 추가
        for (int i = 0; i < 5; i++) {
            group.increaseParticipants();
        }

        // then
        assertThat(group.getGroupStatus()).isEqualTo(GroupStatus.RECRUITING);
    }
}

package com.example.green.domain.challenge.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.green.domain.challenge.controller.dto.ChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.ChallengeParticipationStatus;
import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.entity.vo.GroupAddress;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.PersonalChallengeParticipationRepository;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeParticipationRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.domain.challengecert.entity.TeamChallengeGroupParticipation;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.challengecert.repository.TeamChallengeGroupParticipationRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberRole;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.utils.TimeUtils;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

	@InjectMocks
	private ChallengeService challengeService;

	@Mock
	private PersonalChallengeRepository personalChallengeRepository;

	@Mock
	private TeamChallengeRepository teamChallengeRepository;

	@Mock
	private PersonalChallengeParticipationRepository personalChallengeParticipationRepository;

	@Mock
	private TeamChallengeParticipationRepository teamChallengeParticipationRepository;

	@Mock
	private TeamChallengeGroupParticipationRepository teamChallengeGroupParticipationRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private TimeUtils timeUtils;

	private PersonalChallenge testPersonalChallenge;
	private TeamChallenge testTeamChallenge;
	private TeamChallengeGroup testTeamChallengeGroup;
	private Member testMember;
	private PrincipalDetails principalDetails;
	private static final Long TEST_MEMBER_ID = 1L;
	private static final Long TEST_CHALLENGE_ID = 1L;

	@BeforeEach
	void setUp() {
		testMember = createTestMember();
		testPersonalChallenge = createTestPersonalChallenge();
		testTeamChallenge = createTestTeamChallenge();
		principalDetails = createPrincipalDetails(TEST_MEMBER_ID);

		// 리플렉션으로 ID 설정
		ReflectionTestUtils.setField(testMember, "id", TEST_MEMBER_ID);
		ReflectionTestUtils.setField(testPersonalChallenge, "id", TEST_CHALLENGE_ID);
		ReflectionTestUtils.setField(testTeamChallenge, "id", TEST_CHALLENGE_ID + 1);

		// TeamChallengeGroup은 TeamChallenge가 생성된 후에 생성
		testTeamChallengeGroup = createTestTeamChallengeGroup();
		ReflectionTestUtils.setField(testTeamChallengeGroup, "id", TEST_CHALLENGE_ID + 2);

		// TimeUtils mock 설정 (lenient 모드로 설정하여 불필요한 stubbing 허용)
		lenient().when(timeUtils.now()).thenReturn(LocalDateTime.now());
	}

	@Test
	void 로그인한_사용자가_참여한_팀_챌린지를_조회하면_JOINED_상태를_반환한다() {
		// given
		given(teamChallengeRepository.findById(TEST_CHALLENGE_ID + 1))
			.willReturn(Optional.of(testTeamChallenge));
		given(memberRepository.findById(TEST_MEMBER_ID))
			.willReturn(Optional.of(testMember));
		given(teamChallengeParticipationRepository.existsByMemberAndTeamChallenge(testMember, testTeamChallenge))
			.willReturn(true);

		// when
		ChallengeDetailResponseDto result = challengeService.getChallengeDetail(TEST_CHALLENGE_ID + 1,
			principalDetails);

		// then
		assertThat(result.getId()).isEqualTo(TEST_CHALLENGE_ID + 1);
		assertThat(result.getTitle()).isEqualTo(testTeamChallenge.getChallengeName());
		assertThat(result.getParticipationStatus()).isEqualTo(ChallengeParticipationStatus.JOINED);
		verify(teamChallengeRepository).findById(TEST_CHALLENGE_ID + 1);
		verify(memberRepository).findById(TEST_MEMBER_ID);
		verify(teamChallengeParticipationRepository).existsByMemberAndTeamChallenge(testMember, testTeamChallenge);
	}

	@Test
	void 팀_챌린지_참여_시_참여_정보만_생성된다() {
		// given
		given(teamChallengeRepository.findById(TEST_CHALLENGE_ID + 1))
			.willReturn(Optional.of(testTeamChallenge));
		given(memberRepository.findById(TEST_MEMBER_ID))
			.willReturn(Optional.of(testMember));
		given(teamChallengeParticipationRepository.existsByMemberAndTeamChallenge(testMember, testTeamChallenge))
			.willReturn(false);

		// when
		challengeService.joinChallenge(TEST_CHALLENGE_ID + 1, principalDetails);

		// then
		verify(teamChallengeParticipationRepository).save(any(TeamChallengeParticipation.class));
		// 그룹 참여 정보는 더 이상 자동으로 생성되지 않음
		verify(teamChallengeGroupParticipationRepository, never()).save(any(TeamChallengeGroupParticipation.class));
	}

	@Test
	void 그룹에_참여한_사용자는_팀_챌린지에서_탈퇴할_수_없다() {
		// given
		TeamChallengeParticipation participation = TeamChallengeParticipation.create(
			testTeamChallenge,
			testMember,
			LocalDateTime.now()
		);

		given(teamChallengeRepository.findById(TEST_CHALLENGE_ID + 1))
			.willReturn(Optional.of(testTeamChallenge));
		given(memberRepository.findById(TEST_MEMBER_ID))
			.willReturn(Optional.of(testMember));
		given(teamChallengeParticipationRepository.findByMemberAndTeamChallenge(testMember, testTeamChallenge))
			.willReturn(Optional.of(participation));
		given(teamChallengeGroupParticipationRepository.existsByTeamChallengeParticipation(participation))
			.willReturn(true); // 그룹 참여 중

		// when & then
		assertThatThrownBy(() -> challengeService.leaveChallenge(TEST_CHALLENGE_ID + 1, principalDetails))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CANNOT_LEAVE_WHILE_IN_GROUP);

		// 탈퇴 처리가 되지 않았는지 확인
		verify(teamChallengeParticipationRepository, never()).delete(any());
	}

	@Test
	void 그룹_참여_정보가_없는_사용자도는_팀_챌린지에서_탈퇴할_수_있다() {
		// given
		TeamChallengeParticipation participation = TeamChallengeParticipation.create(
			testTeamChallenge,
			testMember,
			LocalDateTime.now()
		);

		given(teamChallengeRepository.findById(TEST_CHALLENGE_ID + 1))
			.willReturn(Optional.of(testTeamChallenge));
		given(memberRepository.findById(TEST_MEMBER_ID))
			.willReturn(Optional.of(testMember));
		given(teamChallengeParticipationRepository.findByMemberAndTeamChallenge(testMember, testTeamChallenge))
			.willReturn(Optional.of(participation));
		given(teamChallengeGroupParticipationRepository.existsByTeamChallengeParticipation(participation))
			.willReturn(false); // 그룹 참여 정보 없음

		// when
		challengeService.leaveChallenge(TEST_CHALLENGE_ID + 1, principalDetails);

		// then
		verify(teamChallengeParticipationRepository).delete(participation);
	}

	private Member createTestMember() {
		return Member.create("google 123456789", "테스트유저", "test@example.com");
	}

	private PersonalChallenge createTestPersonalChallenge() {
		return PersonalChallenge.create(
			CodeGenerator.generate(ChallengeType.PERSONAL, LocalDateTime.now()),
			"테스트 개인 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(1000)),
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(7),
			"challenge-image.jpg",
			"챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);
	}

	private TeamChallenge createTestTeamChallenge() {
		return TeamChallenge.create(
			CodeGenerator.generate(ChallengeType.TEAM, LocalDateTime.now()),
			"테스트 팀 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(2000)),
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(7),
			5,
			"challenge-image.jpg",
			"팀 챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);
	}

	private TeamChallengeGroup createTestTeamChallengeGroup() {
		return TeamChallengeGroup.create(
			"team code",
			"테스트 그룹",
			LocalDateTime.now().minusHours(2),
			LocalDateTime.now().plusDays(6),
			10,
			GroupAddress.of("서울시 강남구"),
			"테스트 그룹 설명",
			"https://openchat.example.com",
			testTeamChallenge
		);
	}

	private PrincipalDetails createPrincipalDetails(Long memberId) {
		return new PrincipalDetails(memberId, "memberKey", MemberRole.USER.getDescription(), "testuser",
			"testuser@gmail.com");
	}
}

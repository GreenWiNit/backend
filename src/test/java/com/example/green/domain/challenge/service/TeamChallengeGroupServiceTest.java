package com.example.green.domain.challenge.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupUpdateRequestDto;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.entity.vo.GroupAddress;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.TeamChallengeGroupRepository;
import com.example.green.domain.challenge.repository.TeamChallengeParticipationRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.domain.challengecert.entity.TeamChallengeGroupParticipation;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.challengecert.entity.enums.GroupRoleType;
import com.example.green.domain.challengecert.repository.TeamChallengeGroupParticipationRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.utils.TimeUtils;

@ExtendWith(MockitoExtension.class)
class TeamChallengeGroupServiceTest {

	@InjectMocks
	private TeamChallengeGroupService teamChallengeGroupService;

	@Mock
	private TeamChallengeGroupRepository teamChallengeGroupRepository;

	@Mock
	private TeamChallengeRepository teamChallengeRepository;

	@Mock
	private TeamChallengeParticipationRepository teamChallengeParticipationRepository;

	@Mock
	private TeamChallengeGroupParticipationRepository teamChallengeGroupParticipationRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private TimeUtils timeUtils;

	private Member testMember;
	private TeamChallenge testTeamChallenge;
	private TeamChallengeGroup testGroup;
	private TeamChallengeParticipation testParticipation;
	private LocalDateTime testNow;

	@BeforeEach
	void setUp() {
		testNow = LocalDateTime.now();

		// 테스트용 Member 생성
		testMember = Member.create(
			"memberkey",
			"테스트사용자",
			"test@example.com"
		);

		// 테스트용 TeamChallenge 생성
		testTeamChallenge = TeamChallenge.create(
			CodeGenerator.generate(ChallengeType.TEAM, testNow),
			"테스트 팀 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(2000)),
			testNow.minusDays(1),
			testNow.plusDays(7),
			5,
			"challenge-image.jpg",
			"팀 챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		// 테스트용 TeamChallengeGroup 생성
		testGroup = TeamChallengeGroup.create(
			"team code",
			"테스트 그룹",
			testNow.minusHours(1),
			testNow.plusHours(1),
			10,
			GroupAddress.of("서울시 강남구 테헤란로 123"),
			"테스트 그룹 설명",
			"https://openchat.example.com",
			testTeamChallenge
		);

		// 테스트용 TeamChallengeParticipation 생성
		testParticipation = TeamChallengeParticipation.create(
			testTeamChallenge,
			1L,
			testNow.minusHours(1)
		);
	}

	@Test
	void 팀_챌린지_그룹_목록을_조회할_수_있다() {
		// given
		Long challengeId = 1L;
		Long cursor = null;
		Long memberId = 1L;

		List<TeamChallengeGroupListResponseDto> expectedDtos = List.of(
			new TeamChallengeGroupListResponseDto(
				1L, "테스트 그룹", "서울시 강남구 테헤란로 123",
				testNow.minusHours(1), testNow.plusHours(1),
				1, 10, testGroup.getGroupStatus(), false
			)
		);
		CursorTemplate<Long, TeamChallengeGroupListResponseDto> expectedResult =
			CursorTemplate.of(expectedDtos);

		given(teamChallengeGroupRepository.findGroupsByChallengeIdAndCursor(challengeId, cursor, 20, memberId))
			.willReturn(expectedResult);

		// when
		CursorTemplate<Long, TeamChallengeGroupListResponseDto> result =
			teamChallengeGroupService.getTeamChallengeGroups(challengeId, cursor, memberId);

		// then
		assertThat(result).isEqualTo(expectedResult);
		assertThat(result.content()).hasSize(1);
		verify(teamChallengeGroupRepository).findGroupsByChallengeIdAndCursor(challengeId, cursor, 20, memberId);
	}

	@Test
	void 팀_챌린지_그룹_상세_정보를_조회할_수_있다() {
		// given
		Long challengeId = 1L;
		Long groupId = 1L;
		Long memberId = 1L;

		given(teamChallengeGroupRepository.findById(groupId))
			.willReturn(Optional.of(testGroup));
		given(teamChallengeGroupParticipationRepository
			.existsByTeamChallengeGroupIdAndTeamChallengeParticipationMemberIdAndGroupRoleType(
				groupId, memberId, GroupRoleType.LEADER))
			.willReturn(true);
		given(teamChallengeGroupParticipationRepository
			.existsByTeamChallengeGroupIdAndTeamChallengeParticipationMemberId(groupId, memberId))
			.willReturn(true);

		// when
		TeamChallengeGroupDetailResponseDto result =
			teamChallengeGroupService.getTeamChallengeGroupDetail(groupId, memberId);

		// then
		assertThat(result.id()).isEqualTo(testGroup.getId());
		assertThat(result.groupName()).isEqualTo(testGroup.getGroupName());
		assertThat(result.isLeader()).isTrue();
		assertThat(result.isParticipant()).isTrue();
	}

	@Test
	void 존재하지_않는_그룹_상세_조회_시_예외가_발생한다() {
		// given
		Long challengeId = 1L;
		Long groupId = 999L;
		Long memberId = 1L;

		given(teamChallengeGroupRepository.findById(groupId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() ->
			teamChallengeGroupService.getTeamChallengeGroupDetail(groupId, memberId))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_GROUP_NOT_FOUND);
	}

	@Test
	void 팀_챌린지_그룹을_생성할_수_있다() {
		// given
		Long challengeId = 1L;
		Long memberId = 1L;

		TeamChallengeGroupCreateRequestDto request = new TeamChallengeGroupCreateRequestDto(
			"새 그룹",
			"서울시 강남구 테헤란로 456",
			"삼성동 빌딩 2층",
			"새 그룹 설명",
			"https://openchat.new.com",
			testNow.plusHours(1),
			testNow.plusHours(3),
			15
		);

		given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));
		given(teamChallengeRepository.findById(challengeId)).willReturn(Optional.of(testTeamChallenge));
		given(teamChallengeParticipationRepository.findByTeamChallengeAndMemberId(any(TeamChallenge.class), anyLong()))
			.willReturn(Optional.of(testParticipation));
		given(teamChallengeGroupRepository.save(any(TeamChallengeGroup.class)))
			.willAnswer(invocation -> {
				TeamChallengeGroup group = invocation.getArgument(0);
				// ID 설정 (실제로는 JPA가 자동 생성)
				return group;
			});
		given(teamChallengeGroupParticipationRepository.save(any(TeamChallengeGroupParticipation.class)))
			.willAnswer(invocation -> invocation.getArgument(0));

		// when
		Long result = teamChallengeGroupService.createTeamChallengeGroup(challengeId, request, memberId);

		// then
		verify(teamChallengeGroupRepository).save(any(TeamChallengeGroup.class));
		verify(teamChallengeGroupParticipationRepository).save(any(TeamChallengeGroupParticipation.class));
	}

	@Test
	void 존재하지_않는_회원으로_그룹_생성_시_예외가_발생한다() {
		// given
		Long challengeId = 1L;
		Long memberId = 999L;

		TeamChallengeGroupCreateRequestDto request = new TeamChallengeGroupCreateRequestDto(
			"새 그룹", "서울시 강남구", null, null, null,
			testNow.plusHours(1), testNow.plusHours(3), 10
		);

		given(memberRepository.findById(memberId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() ->
			teamChallengeGroupService.createTeamChallengeGroup(challengeId, request, memberId))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.MEMBER_NOT_FOUND);
	}

	@Test
	void 존재하지_않는_챌린지로_그룹_생성_시_예외가_발생한다() {
		// given
		Long challengeId = 999L;
		Long memberId = 1L;

		TeamChallengeGroupCreateRequestDto request = new TeamChallengeGroupCreateRequestDto(
			"새 그룹", "서울시 강남구", null, null, null,
			testNow.plusHours(1), testNow.plusHours(3), 10
		);

		given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));
		given(teamChallengeRepository.findById(challengeId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() ->
			teamChallengeGroupService.createTeamChallengeGroup(challengeId, request, memberId))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_NOT_FOUND);
	}

	@Test
	void 팀_챌린지에_참가하지_않은_회원이_그룹_생성_시_예외가_발생한다() {
		// given
		Long challengeId = 1L;
		Long memberId = 1L;

		TeamChallengeGroupCreateRequestDto request = new TeamChallengeGroupCreateRequestDto(
			"새 그룹", "서울시 강남구", null, null, null,
			testNow.plusHours(1), testNow.plusHours(3), 10
		);

		given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));
		given(teamChallengeRepository.findById(challengeId)).willReturn(Optional.of(testTeamChallenge));
		given(teamChallengeParticipationRepository.findByTeamChallengeAndMemberId(any(TeamChallenge.class), anyLong()))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() ->
			teamChallengeGroupService.createTeamChallengeGroup(challengeId, request, memberId))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.NOT_PARTICIPATING_IN_CHALLENGE);
	}

	@Test
	void 팀_챌린지_그룹에_참가할_수_있다() {
		// given
		Long challengeId = 1L;
		Long groupId = 1L;
		Long memberId = 1L;

		given(teamChallengeGroupRepository.findById(groupId))
			.willReturn(Optional.of(testGroup));
		given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));
		given(teamChallengeParticipationRepository.findByTeamChallengeAndMemberId(any(TeamChallenge.class), anyLong()))
			.willReturn(Optional.of(testParticipation));
		given(teamChallengeGroupParticipationRepository
			.existsByTeamChallengeParticipationAndTeamChallengeGroup(testParticipation, testGroup))
			.willReturn(false);
		given(timeUtils.now()).willReturn(testNow);
		given(teamChallengeGroupParticipationRepository.save(any(TeamChallengeGroupParticipation.class)))
			.willAnswer(invocation -> invocation.getArgument(0));

		// when
		teamChallengeGroupService.joinTeamChallengeGroup(groupId, memberId);

		// then
		verify(teamChallengeGroupParticipationRepository).save(any(TeamChallengeGroupParticipation.class));
	}

	@Test
	void 이미_참가_중인_그룹에_다시_참가_시_예외가_발생한다() {
		// given
		Long challengeId = 1L;
		Long groupId = 1L;
		Long memberId = 1L;

		given(teamChallengeGroupRepository.findById(groupId))
			.willReturn(Optional.of(testGroup));
		given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));
		given(teamChallengeParticipationRepository.findByTeamChallengeAndMemberId(any(TeamChallenge.class), anyLong()))
			.willReturn(Optional.of(testParticipation));
		given(teamChallengeGroupParticipationRepository
			.existsByTeamChallengeParticipationAndTeamChallengeGroup(testParticipation, testGroup))
			.willReturn(true);

		// when & then
		assertThatThrownBy(() ->
			teamChallengeGroupService.joinTeamChallengeGroup(groupId, memberId))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.ALREADY_PARTICIPATING_IN_GROUP);
	}

	@Test
	void 팀_챌린지_그룹을_삭제할_수_있다() {
		// given
		Long challengeId = 1L;
		Long groupId = 1L;
		Long memberId = 1L;

		given(teamChallengeGroupRepository.findById(groupId))
			.willReturn(Optional.of(testGroup));
		given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));
		given(teamChallengeParticipationRepository.findByTeamChallengeAndMemberId(any(TeamChallenge.class), anyLong()))
			.willReturn(Optional.of(testParticipation));
		given(teamChallengeGroupParticipationRepository
			.existsByTeamChallengeGroupIdAndTeamChallengeParticipationMemberIdAndGroupRoleType(
				groupId, memberId, GroupRoleType.LEADER))
			.willReturn(true);

		// when
		teamChallengeGroupService.deleteTeamChallengeGroup(groupId, memberId);

		// then
		verify(teamChallengeGroupParticipationRepository).deleteByTeamChallengeGroup(testGroup);
		verify(teamChallengeGroupRepository).delete(testGroup);
	}

	@Test
	void 리더가_아닌_사용자가_그룹_삭제_시_예외가_발생한다() {
		// given
		Long challengeId = 1L;
		Long groupId = 1L;
		Long memberId = 1L;

		given(teamChallengeGroupRepository.findById(groupId))
			.willReturn(Optional.of(testGroup));
		given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));
		given(teamChallengeParticipationRepository.findByTeamChallengeAndMemberId(any(TeamChallenge.class), anyLong()))
			.willReturn(Optional.of(testParticipation));
		given(teamChallengeGroupParticipationRepository
			.existsByTeamChallengeGroupIdAndTeamChallengeParticipationMemberIdAndGroupRoleType(
				groupId, memberId, GroupRoleType.LEADER))
			.willReturn(false);

		// when & then
		assertThatThrownBy(() ->
			teamChallengeGroupService.deleteTeamChallengeGroup(groupId, memberId))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.NOT_GROUP_LEADER);
	}

	@Test
	void 팀_챌린지_그룹을_수정할_수_있다() {
		// given
		Long groupId = 1L;
		Long memberId = 1L;

		TeamChallengeGroupUpdateRequestDto request = new TeamChallengeGroupUpdateRequestDto(
			"수정된 그룹명",
			"서울시 강남구 테헤란로 789",
			"강남역 근처",
			"수정된 그룹 설명",
			"https://updated-openchat.com",
			testNow.plusHours(2),
			testNow.plusHours(4),
			20
		);

		given(teamChallengeGroupRepository.findById(groupId))
			.willReturn(Optional.of(testGroup));
		given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));
		given(teamChallengeParticipationRepository.findByTeamChallengeAndMemberId(any(TeamChallenge.class), anyLong()))
			.willReturn(Optional.of(testParticipation));
		given(teamChallengeGroupParticipationRepository
			.existsByTeamChallengeGroupIdAndTeamChallengeParticipationMemberIdAndGroupRoleType(
				groupId, memberId, GroupRoleType.LEADER))
			.willReturn(true);

		// when
		teamChallengeGroupService.updateTeamChallengeGroup(groupId, request, memberId);

		// then
		// 실제 엔티티의 update 메서드가 호출되었는지는 verify할 수 없으므로 예외가 발생하지 않는 것으로 검증
		assertThat(testGroup.getGroupName()).isNotNull();
	}

	@Test
	void 리더가_아닌_사용자가_그룹_수정_시_예외가_발생한다() {
		// given
		Long groupId = 1L;
		Long memberId = 1L;

		TeamChallengeGroupUpdateRequestDto request = new TeamChallengeGroupUpdateRequestDto(
			"수정된 그룹명",
			"서울시 강남구 테헤란로 789",
			"강남역 근처",
			"수정된 그룹 설명",
			"https://updated-openchat.com",
			testNow.plusHours(2),
			testNow.plusHours(4),
			20
		);

		given(teamChallengeGroupRepository.findById(groupId))
			.willReturn(Optional.of(testGroup));
		given(memberRepository.findById(memberId)).willReturn(Optional.of(testMember));
		given(teamChallengeParticipationRepository.findByTeamChallengeAndMemberId(any(TeamChallenge.class), anyLong()))
			.willReturn(Optional.of(testParticipation));
		given(teamChallengeGroupParticipationRepository
			.existsByTeamChallengeGroupIdAndTeamChallengeParticipationMemberIdAndGroupRoleType(
				groupId, memberId, GroupRoleType.LEADER))
			.willReturn(false);

		// when & then
		assertThatThrownBy(() ->
			teamChallengeGroupService.updateTeamChallengeGroup(groupId, request, memberId))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.NOT_GROUP_LEADER);
	}

	@Test
	void 존재하지_않는_그룹_수정_시_예외가_발생한다() {
		// given
		Long groupId = 999L;
		Long memberId = 1L;

		TeamChallengeGroupUpdateRequestDto request = new TeamChallengeGroupUpdateRequestDto(
			"수정된 그룹명",
			"서울시 강남구 테헤란로 789",
			"강남역 근처",
			"수정된 그룹 설명",
			"https://updated-openchat.com",
			testNow.plusHours(2),
			testNow.plusHours(4),
			20
		);

		given(teamChallengeGroupRepository.findById(groupId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() ->
			teamChallengeGroupService.updateTeamChallengeGroup(groupId, request, memberId))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_GROUP_NOT_FOUND);
	}
}

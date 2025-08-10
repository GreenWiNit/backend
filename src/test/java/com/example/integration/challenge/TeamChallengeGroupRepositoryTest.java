package com.example.integration.challenge;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.entity.vo.GroupAddress;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
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
import com.example.integration.common.BaseIntegrationTest;

@Transactional
class TeamChallengeGroupRepositoryTest extends BaseIntegrationTest {

	@Autowired
	private TeamChallengeGroupRepository teamChallengeGroupRepository;

	@Autowired
	private TeamChallengeRepository teamChallengeRepository;

	@Autowired
	private TeamChallengeParticipationRepository teamChallengeParticipationRepository;

	@Autowired
	private TeamChallengeGroupParticipationRepository teamChallengeGroupParticipationRepository;

	@Autowired
	private MemberRepository memberRepository;

	private Member testMember;
	private TeamChallenge testTeamChallenge;
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
		testMember = memberRepository.save(testMember);

		// 테스트용 TeamChallenge 생성
		testTeamChallenge = TeamChallenge.create(
			CodeGenerator.generateChallengeCode(ChallengeType.TEAM, testNow),
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
		testTeamChallenge = teamChallengeRepository.save(testTeamChallenge);
	}

	@Test
	void 특정_팀_챌린지의_그룹_목록을_커서_기반으로_조회할_수_있다() {
		// given
		Long challengeId = testTeamChallenge.getId();
		Long memberId = testMember.getId();

		// 테스트용 그룹들 생성
		TeamChallengeGroup group1 = createTestGroup("그룹 1", "서울시 강남구 테헤란로 123");
		TeamChallengeGroup group2 = createTestGroup("그룹 2", "서울시 강남구 테헤란로 456");
		TeamChallengeGroup group3 = createTestGroup("그룹 3", "서울시 강남구 테헤란로 789");

		// 첫 번째 그룹에만 리더로 참가
		createParticipationAndGroupLeader(group1, memberId);

		// when - 커서 없이 첫 번째 페이지 조회
		CursorTemplate<Long, TeamChallengeGroupListResponseDto> result =
			teamChallengeGroupRepository.findGroupsByChallengeIdAndCursor(challengeId, null, 2, memberId);

		// then
		assertThat(result.hasNext()).isTrue();
		assertThat(result.content()).hasSize(2);
		assertThat(result.nextCursor()).isNotNull();

		// ID 내림차순으로 정렬되어야 함
		List<TeamChallengeGroupListResponseDto> content = result.content();
		assertThat(content.get(0).id()).isGreaterThan(content.get(1).id());

		// 첫 번째 그룹에서만 리더여야 함
		TeamChallengeGroupListResponseDto firstGroup = content.stream()
			.filter(dto -> dto.id().equals(group1.getId()))
			.findFirst()
			.orElse(null);

		if (firstGroup != null) {
			assertThat(firstGroup.isLeader()).isTrue();
		}
	}

	@Test
	void 커서를_사용하여_다음_페이지를_조회할_수_있다() {
		// given
		Long challengeId = testTeamChallenge.getId();
		Long memberId = testMember.getId();

		// 테스트용 그룹들 생성 (5개)
		for (int i = 1; i <= 5; i++) {
			createTestGroup("그룹 " + i, "서울시 강남구 테헤란로 " + (100 + i));
		}

		// when - 첫 번째 페이지 조회 (2개)
		CursorTemplate<Long, TeamChallengeGroupListResponseDto> firstPage =
			teamChallengeGroupRepository.findGroupsByChallengeIdAndCursor(challengeId, null, 2, memberId);

		// then
		assertThat(firstPage.hasNext()).isTrue();
		assertThat(firstPage.content()).hasSize(2);
		Long cursor = firstPage.nextCursor();

		// when - 두 번째 페이지 조회
		CursorTemplate<Long, TeamChallengeGroupListResponseDto> secondPage =
			teamChallengeGroupRepository.findGroupsByChallengeIdAndCursor(challengeId, cursor, 2, memberId);

		// then
		assertThat(secondPage.hasNext()).isTrue();
		assertThat(secondPage.content()).hasSize(2);

		// 첫 번째 페이지와 두 번째 페이지의 ID가 겹치지 않아야 함
		List<Long> firstPageIds = firstPage.content().stream().map(TeamChallengeGroupListResponseDto::id).toList();
		List<Long> secondPageIds = secondPage.content().stream().map(TeamChallengeGroupListResponseDto::id).toList();
		assertThat(firstPageIds).doesNotContainAnyElementsOf(secondPageIds);
	}

	@Test
	void 그룹이_없을_때_빈_결과를_반환한다() {
		// given
		Long challengeId = testTeamChallenge.getId();
		Long memberId = testMember.getId();

		// when
		CursorTemplate<Long, TeamChallengeGroupListResponseDto> result =
			teamChallengeGroupRepository.findGroupsByChallengeIdAndCursor(challengeId, null, 20, memberId);

		// then
		assertThat(result.hasNext()).isFalse();
		assertThat(result.content()).isEmpty();
		assertThat(result.nextCursor()).isNull();
	}

	@Test
	void 리더_여부가_올바르게_조회된다() {
		// given
		Long challengeId = testTeamChallenge.getId();
		Long memberId = testMember.getId();

		// 추가 멤버 생성
		Member memberForGroup2 = Member.create("memberkey2", "멤버2", "test2@example.com");
		memberForGroup2 = memberRepository.save(memberForGroup2);

		TeamChallengeGroup leaderGroup = createTestGroup("리더 그룹", "서울시 강남구 테헤란로 100");
		TeamChallengeGroup memberGroup = createTestGroup("멤버 그룹", "서울시 강남구 테헤란로 200");
		TeamChallengeGroup noParticipationGroup = createTestGroup("미참가 그룹", "서울시 강남구 테헤란로 300");

		// 첫 번째 그룹에는 testMember를 리더로 참가
		createParticipationAndGroupLeader(leaderGroup, memberId);

		// 두 번째 그룹에는 다른 멤버를 멤버로 참가 (testMember는 조회만 함)
		createParticipationAndGroupMember(memberGroup, memberForGroup2.getId());

		// when
		CursorTemplate<Long, TeamChallengeGroupListResponseDto> result =
			teamChallengeGroupRepository.findGroupsByChallengeIdAndCursor(challengeId, null, 10, memberId);

		// then
		assertThat(result.content()).hasSize(3);

		for (TeamChallengeGroupListResponseDto dto : result.content()) {
			if (dto.id().equals(leaderGroup.getId())) {
				assertThat(dto.isLeader()).isTrue();
			} else {
				// testMember는 다른 그룹에는 참가하지 않았으므로 리더가 아님
				assertThat(dto.isLeader()).isFalse();
			}
		}
	}

	private TeamChallengeGroup createTestGroup(String groupName, String address) {
		TeamChallengeGroup group = TeamChallengeGroup.create(
			"team code",
			groupName,
			testNow.minusHours(1),
			testNow.plusHours(1),
			10,
			GroupAddress.of(address),
			"테스트 그룹 설명",
			"https://openchat.example.com",
			testTeamChallenge
		);
		return teamChallengeGroupRepository.save(group);
	}

	private void createParticipationAndGroupLeader(TeamChallengeGroup group, Long memberId) {
		TeamChallengeParticipation participation = createTeamChallengeParticipation(memberId);
		TeamChallengeGroupParticipation groupParticipation = TeamChallengeGroupParticipation.create(
			participation, group, GroupRoleType.LEADER
		);
		teamChallengeGroupParticipationRepository.save(groupParticipation);
	}

	private void createParticipationAndGroupMember(TeamChallengeGroup group, Long memberId) {
		TeamChallengeParticipation participation = createTeamChallengeParticipation(memberId);
		TeamChallengeGroupParticipation groupParticipation = TeamChallengeGroupParticipation.create(
			participation, group, GroupRoleType.MEMBER
		);
		teamChallengeGroupParticipationRepository.save(groupParticipation);
	}

	private TeamChallengeParticipation createTeamChallengeParticipation(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow();

		// 기존 참가 정보가 있는지 확인
		return teamChallengeParticipationRepository
			.findByTeamChallengeAndMemberId(testTeamChallenge, member.getId())
			.orElseGet(() -> {
				TeamChallengeParticipation participation = TeamChallengeParticipation.create(
					testTeamChallenge, member.getId(), testNow.minusHours(1)
				);
				return teamChallengeParticipationRepository.save(participation);
			});
	}
}

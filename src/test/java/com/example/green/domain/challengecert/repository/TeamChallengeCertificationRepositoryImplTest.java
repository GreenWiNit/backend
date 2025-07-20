package com.example.green.domain.challengecert.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.utils.ChallengeCodeGenerator;
import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.domain.challengecert.entity.TeamChallengeCertification;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.api.page.CursorTemplate;
import com.example.integration.common.BaseIntegrationTest;

@Transactional
class TeamChallengeCertificationRepositoryImplTest extends BaseIntegrationTest {

	@Autowired
	private TeamChallengeCertificationRepository teamChallengeCertificationRepository;

	@Autowired
	private TeamChallengeParticipationRepository teamChallengeParticipationRepository;

	@Autowired
	private TeamChallengeRepository teamChallengeRepository;

	@Autowired
	private MemberRepository memberRepository;

	private Member testMember1;
	private Member testMember2;
	private TeamChallenge testChallenge;
	private TeamChallengeParticipation testParticipation1;
	private TeamChallengeParticipation testParticipation2;
	private LocalDateTime testNow;

	@BeforeEach
	void setUp() {
		testNow = LocalDateTime.now();

		// 테스트용 Members 생성
		testMember1 = Member.create("member1", "테스트 사용자 1", "test1@example.com");
		testMember2 = Member.create("member2", "테스트 사용자 2", "test2@example.com");
		testMember1 = memberRepository.save(testMember1);
		testMember2 = memberRepository.save(testMember2);

		// 테스트용 TeamChallenge 생성
		testChallenge = TeamChallenge.create(
			ChallengeCodeGenerator.generate(ChallengeType.TEAM, testNow),
			"테스트 팀 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(2000)),
			testNow.minusDays(7),
			testNow.plusDays(7),
			5,
			"team-challenge-image.jpg",
			"팀 챌린지 설명"
		);
		testChallenge = teamChallengeRepository.save(testChallenge);

		// 테스트용 Participations 생성
		testParticipation1 = TeamChallengeParticipation.create(testChallenge, testMember1, testNow.minusDays(5));
		testParticipation2 = TeamChallengeParticipation.create(testChallenge, testMember2, testNow.minusDays(4));
		testParticipation1 = teamChallengeParticipationRepository.save(testParticipation1);
		testParticipation2 = teamChallengeParticipationRepository.save(testParticipation2);
	}

	@Test
	void findByMemberWithCursor_첫_페이지_조회시_올바른_결과를_반환한다() {
		// given
		TeamChallengeCertification cert1 = createCertification(testParticipation1, "팀이미지1.jpg", "팀후기1",
			testNow.minusDays(3), testNow.minusDays(3).toLocalDate());
		TeamChallengeCertification cert2 = createCertification(testParticipation1, "팀이미지2.jpg", "팀후기2",
			testNow.minusDays(2), testNow.minusDays(2).toLocalDate());
		TeamChallengeCertification cert3 = createCertification(testParticipation1, "팀이미지3.jpg", "팀후기3",
			testNow.minusDays(1), testNow.minusDays(1).toLocalDate());

		// when
		CursorTemplate<Long, ChallengeCertificationListResponseDto> result =
			teamChallengeCertificationRepository.findByMemberWithCursor(testMember1, null, 2);

		// then
		assertThat(result.hasNext()).isTrue();
		assertThat(result.nextCursor()).isEqualTo(cert2.getId());
		assertThat(result.content()).hasSize(2);

		// ID 기준 내림차순 정렬 확인
		assertThat(result.content().get(0).certificationId()).isEqualTo(cert3.getId());
		assertThat(result.content().get(1).certificationId()).isEqualTo(cert2.getId());

		// DTO 필드 검증
		ChallengeCertificationListResponseDto firstDto = result.content().get(0);
		assertThat(firstDto.challengeId()).isEqualTo(testChallenge.getId());
		assertThat(firstDto.challengeTitle()).isEqualTo("테스트 팀 챌린지");
		assertThat(firstDto.approved()).isFalse(); // 기본값
	}

	@Test
	void findByMemberWithCursor_다음_페이지_조회시_올바른_결과를_반환한다() {
		// given
		TeamChallengeCertification cert1 = createCertification(testParticipation1, "팀이미지1.jpg", "팀후기1",
			testNow.minusDays(3), testNow.minusDays(3).toLocalDate());
		TeamChallengeCertification cert2 = createCertification(testParticipation1, "팀이미지2.jpg", "팀후기2",
			testNow.minusDays(2), testNow.minusDays(2).toLocalDate());
		TeamChallengeCertification cert3 = createCertification(testParticipation1, "팀이미지3.jpg", "팀후기3",
			testNow.minusDays(1), testNow.minusDays(1).toLocalDate());

		// when
		CursorTemplate<Long, ChallengeCertificationListResponseDto> result =
			teamChallengeCertificationRepository.findByMemberWithCursor(testMember1, cert2.getId(), 2);

		// then
		assertThat(result.hasNext()).isFalse();
		assertThat(result.nextCursor()).isNull();
		assertThat(result.content()).hasSize(1);
		assertThat(result.content().get(0).certificationId()).isEqualTo(cert1.getId());
	}

	@Test
	void findByMemberWithCursor_마지막_페이지일_때_hasNext가_false이다() {
		// given
		TeamChallengeCertification cert1 = createCertification(testParticipation1, "팀이미지1.jpg", "팀후기1",
			testNow.minusDays(1), testNow.minusDays(1).toLocalDate());

		// when
		CursorTemplate<Long, ChallengeCertificationListResponseDto> result =
			teamChallengeCertificationRepository.findByMemberWithCursor(testMember1, null, 10);

		// then
		assertThat(result.hasNext()).isFalse();
		assertThat(result.nextCursor()).isNull();
		assertThat(result.content()).hasSize(1);
	}

	@Test
	void findByMemberWithCursor_인증이_없을_때_빈_결과를_반환한다() {
		// when
		CursorTemplate<Long, ChallengeCertificationListResponseDto> result =
			teamChallengeCertificationRepository.findByMemberWithCursor(testMember1, null, 10);

		// then
		assertThat(result.hasNext()).isFalse();
		assertThat(result.nextCursor()).isNull();
		assertThat(result.content()).isEmpty();
	}

	@Test
	void findByMemberWithCursor_다른_회원의_인증은_조회되지_않는다() {
		// given
		createCertification(testParticipation1, "팀이미지1.jpg", "팀후기1", testNow.minusDays(1),
			testNow.minusDays(1).toLocalDate());
		createCertification(testParticipation2, "팀이미지2.jpg", "팀후기2", testNow.minusDays(1),
			testNow.minusDays(1).toLocalDate());

		// when
		CursorTemplate<Long, ChallengeCertificationListResponseDto> result =
			teamChallengeCertificationRepository.findByMemberWithCursor(testMember1, null, 10);

		// then
		assertThat(result.content()).hasSize(1);
		assertThat(result.content().get(0).challengeTitle()).isEqualTo("테스트 팀 챌린지");
	}

	@Test
	void findByIdAndMember_존재하는_인증을_올바르게_조회한다() {
		// given
		TeamChallengeCertification cert = createCertification(testParticipation1, "팀이미지.jpg", "팀후기",
			testNow.minusDays(1), testNow.minusDays(1).toLocalDate());

		// when
		Optional<TeamChallengeCertification> result =
			teamChallengeCertificationRepository.findByIdAndMember(cert.getId(), testMember1);

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(cert.getId());
		assertThat(result.get().getCertificationImageUrl()).isEqualTo("팀이미지.jpg");
		assertThat(result.get().getCertificationReview()).isEqualTo("팀후기");

		// JOIN FETCH 확인 - LazyInitializationException이 발생하지 않아야 함
		assertThat(result.get().getParticipation().getTeamChallenge().getChallengeName()).isEqualTo("테스트 팀 챌린지");
	}

	@Test
	void findByIdAndMember_존재하지_않는_인증은_빈_결과를_반환한다() {
		// when
		Optional<TeamChallengeCertification> result =
			teamChallengeCertificationRepository.findByIdAndMember(999L, testMember1);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	void findByIdAndMember_다른_회원의_인증은_빈_결과를_반환한다() {
		// given
		TeamChallengeCertification cert = createCertification(testParticipation1, "팀이미지.jpg", "팀후기",
			testNow.minusDays(1), testNow.minusDays(1).toLocalDate());

		// when
		Optional<TeamChallengeCertification> result =
			teamChallengeCertificationRepository.findByIdAndMember(cert.getId(), testMember2);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	void findByMemberWithCursor_대용량_데이터에서_정확한_페이징_동작한다() {
		// given - 25개의 인증 데이터 생성
		for (int i = 1; i <= 25; i++) {
			createCertification(
				testParticipation1,
				"이미지" + i + ".jpg",
				"후기" + i,
				testNow.minusDays(i),
				testNow.minusDays(i).toLocalDate()
			);
		}

		// when - 첫 페이지 (20개)
		CursorTemplate<Long, ChallengeCertificationListResponseDto> firstPage =
			teamChallengeCertificationRepository.findByMemberWithCursor(testMember1, null, 20);

		// then - 첫 페이지 검증
		assertThat(firstPage.hasNext()).isTrue();
		assertThat(firstPage.content()).hasSize(20);
		assertThat(firstPage.nextCursor()).isNotNull();

		// when - 두 번째 페이지 (나머지 5개)
		CursorTemplate<Long, ChallengeCertificationListResponseDto> secondPage =
			teamChallengeCertificationRepository.findByMemberWithCursor(testMember1, firstPage.nextCursor(), 20);

		// then - 두 번째 페이지 검증
		assertThat(secondPage.hasNext()).isFalse();
		assertThat(secondPage.content()).hasSize(5);
		assertThat(secondPage.nextCursor()).isNull();
	}

	private TeamChallengeCertification createCertification(
		TeamChallengeParticipation participation,
		String imageUrl,
		String review,
		LocalDateTime certifiedAt,
		LocalDate certifiedDate
	) {
		TeamChallengeCertification certification = TeamChallengeCertification.create(
			participation, imageUrl, review, certifiedAt, certifiedDate
		);
		return teamChallengeCertificationRepository.save(certification);
	}
}

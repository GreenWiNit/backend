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

import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.domain.challengecert.entity.PersonalChallengeCertification;
import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.api.page.CursorTemplate;
import com.example.integration.common.BaseIntegrationTest;

@Transactional
class PersonalChallengeCertificationRepositoryImplTest extends BaseIntegrationTest {

	@Autowired
	private PersonalChallengeCertificationRepository personalChallengeCertificationRepository;

	@Autowired
	private PersonalChallengeParticipationRepository personalChallengeParticipationRepository;

	@Autowired
	private PersonalChallengeRepository personalChallengeRepository;

	@Autowired
	private MemberRepository memberRepository;

	private Member testMember1;
	private Member testMember2;
	private PersonalChallenge testChallenge;
	private PersonalChallengeParticipation testParticipation1;
	private PersonalChallengeParticipation testParticipation2;
	private LocalDateTime testNow;

	@BeforeEach
	void setUp() {
		testNow = LocalDateTime.now();

		// 테스트용 Members 생성
		testMember1 = Member.create("member1", "테스트 사용자 1", "test1@example.com");
		testMember2 = Member.create("member2", "테스트 사용자 2", "test2@example.com");
		testMember1 = memberRepository.save(testMember1);
		testMember2 = memberRepository.save(testMember2);

		// 테스트용 PersonalChallenge 생성
		testChallenge = PersonalChallenge.create(
			CodeGenerator.generate(ChallengeType.PERSONAL, testNow),
			"테스트 개인 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(1000)),
			testNow.minusDays(7),
			testNow.plusDays(7),
			"challenge-image.jpg",
			"개인 챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);
		testChallenge = personalChallengeRepository.save(testChallenge);

		// 테스트용 Participations 생성
		testParticipation1 = PersonalChallengeParticipation.create(testChallenge, testMember1, testNow.minusDays(5));
		testParticipation2 = PersonalChallengeParticipation.create(testChallenge, testMember2, testNow.minusDays(4));
		testParticipation1 = personalChallengeParticipationRepository.save(testParticipation1);
		testParticipation2 = personalChallengeParticipationRepository.save(testParticipation2);
	}

	@Test
	void findByMemberWithCursor_첫_페이지_조회시_올바른_결과를_반환한다() {
		// given
		PersonalChallengeCertification cert1 = createCertification(testParticipation1, "이미지1.jpg", "후기1",
			testNow.minusDays(3), testNow.minusDays(3).toLocalDate());
		PersonalChallengeCertification cert2 = createCertification(testParticipation1, "이미지2.jpg", "후기2",
			testNow.minusDays(2), testNow.minusDays(2).toLocalDate());
		PersonalChallengeCertification cert3 = createCertification(testParticipation1, "이미지3.jpg", "후기3",
			testNow.minusDays(1), testNow.minusDays(1).toLocalDate());

		// when
		CursorTemplate<Long, ChallengeCertificationListResponseDto> result =
			personalChallengeCertificationRepository.findByMemberWithCursor(testMember1, null, 2);

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
		assertThat(firstDto.challengeTitle()).isEqualTo("테스트 개인 챌린지");
		assertThat(firstDto.approved()).isFalse(); // 기본값
	}

	@Test
	void findByMemberWithCursor_다음_페이지_조회시_올바른_결과를_반환한다() {
		// given
		PersonalChallengeCertification cert1 = createCertification(testParticipation1, "이미지1.jpg", "후기1",
			testNow.minusDays(3), testNow.minusDays(3).toLocalDate());
		PersonalChallengeCertification cert2 = createCertification(testParticipation1, "이미지2.jpg", "후기2",
			testNow.minusDays(2), testNow.minusDays(2).toLocalDate());
		PersonalChallengeCertification cert3 = createCertification(testParticipation1, "이미지3.jpg", "후기3",
			testNow.minusDays(1), testNow.minusDays(1).toLocalDate());

		// when
		CursorTemplate<Long, ChallengeCertificationListResponseDto> result =
			personalChallengeCertificationRepository.findByMemberWithCursor(testMember1, cert2.getId(), 2);

		// then
		assertThat(result.hasNext()).isFalse();
		assertThat(result.nextCursor()).isNull();
		assertThat(result.content()).hasSize(1);
		assertThat(result.content().get(0).certificationId()).isEqualTo(cert1.getId());
	}

	@Test
	void findByMemberWithCursor_마지막_페이지일_때_hasNext가_false이다() {
		// given
		PersonalChallengeCertification cert1 = createCertification(testParticipation1, "이미지1.jpg", "후기1",
			testNow.minusDays(1), testNow.minusDays(1).toLocalDate());

		// when
		CursorTemplate<Long, ChallengeCertificationListResponseDto> result =
			personalChallengeCertificationRepository.findByMemberWithCursor(testMember1, null, 10);

		// then
		assertThat(result.hasNext()).isFalse();
		assertThat(result.nextCursor()).isNull();
		assertThat(result.content()).hasSize(1);
	}

	@Test
	void findByMemberWithCursor_인증이_없을_때_빈_결과를_반환한다() {
		// when
		CursorTemplate<Long, ChallengeCertificationListResponseDto> result =
			personalChallengeCertificationRepository.findByMemberWithCursor(testMember1, null, 10);

		// then
		assertThat(result.hasNext()).isFalse();
		assertThat(result.nextCursor()).isNull();
		assertThat(result.content()).isEmpty();
	}

	@Test
	void findByMemberWithCursor_다른_회원의_인증은_조회되지_않는다() {
		// given
		createCertification(testParticipation1, "이미지1.jpg", "후기1", testNow.minusDays(1),
			testNow.minusDays(1).toLocalDate());
		createCertification(testParticipation2, "이미지2.jpg", "후기2", testNow.minusDays(1),
			testNow.minusDays(1).toLocalDate());

		// when
		CursorTemplate<Long, ChallengeCertificationListResponseDto> result =
			personalChallengeCertificationRepository.findByMemberWithCursor(testMember1, null, 10);

		// then
		assertThat(result.content()).hasSize(1);
		assertThat(result.content().get(0).challengeTitle()).isEqualTo("테스트 개인 챌린지");
	}

	@Test
	void findByIdAndMember_존재하는_인증을_올바르게_조회한다() {
		// given
		PersonalChallengeCertification cert = createCertification(testParticipation1, "이미지.jpg", "후기",
			testNow.minusDays(1), testNow.minusDays(1).toLocalDate());

		// when
		Optional<PersonalChallengeCertification> result =
			personalChallengeCertificationRepository.findByIdAndMember(cert.getId(), testMember1);

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(cert.getId());
		assertThat(result.get().getCertificationImageUrl()).isEqualTo("이미지.jpg");
		assertThat(result.get().getCertificationReview()).isEqualTo("후기");

		// JOIN FETCH 확인 - LazyInitializationException이 발생하지 않아야 함
		assertThat(result.get().getParticipation().getPersonalChallenge().getChallengeName()).isEqualTo("테스트 개인 챌린지");
	}

	@Test
	void findByIdAndMember_존재하지_않는_인증은_빈_결과를_반환한다() {
		// when
		Optional<PersonalChallengeCertification> result =
			personalChallengeCertificationRepository.findByIdAndMember(999L, testMember1);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	void findByIdAndMember_다른_회원의_인증은_빈_결과를_반환한다() {
		// given
		PersonalChallengeCertification cert = createCertification(testParticipation1, "이미지.jpg", "후기",
			testNow.minusDays(1), testNow.minusDays(1).toLocalDate());

		// when
		Optional<PersonalChallengeCertification> result =
			personalChallengeCertificationRepository.findByIdAndMember(cert.getId(), testMember2);

		// then
		assertThat(result).isEmpty();
	}

	private PersonalChallengeCertification createCertification(
		PersonalChallengeParticipation participation,
		String imageUrl,
		String review,
		LocalDateTime certifiedAt,
		LocalDate certifiedDate
	) {
		PersonalChallengeCertification certification = PersonalChallengeCertification.create(
			participation, imageUrl, review, certifiedAt, certifiedDate
		);
		return personalChallengeCertificationRepository.save(certification);
	}
}

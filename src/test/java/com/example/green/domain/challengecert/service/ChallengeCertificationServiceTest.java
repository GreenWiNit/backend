package com.example.green.domain.challengecert.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateRequestDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateResponseDto;
import com.example.green.domain.challengecert.entity.PersonalChallengeCertification;
import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;
import com.example.green.domain.challengecert.entity.TeamChallengeCertification;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.challengecert.exception.ChallengeCertException;
import com.example.green.domain.challengecert.exception.ChallengeCertExceptionMessage;
import com.example.green.domain.challengecert.repository.PersonalChallengeCertificationRepository;
import com.example.green.domain.challengecert.repository.PersonalChallengeParticipationRepository;
import com.example.green.domain.challengecert.repository.TeamChallengeCertificationRepository;
import com.example.green.domain.challengecert.repository.TeamChallengeParticipationRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.utils.TimeUtils;

@ExtendWith(MockitoExtension.class)
class ChallengeCertificationServiceTest {

	@InjectMocks
	private ChallengeCertificationService challengeCertificationService;

	@Mock
	private PersonalChallengeRepository personalChallengeRepository;
	@Mock
	private TeamChallengeRepository teamChallengeRepository;
	@Mock
	private PersonalChallengeParticipationRepository personalChallengeParticipationRepository;
	@Mock
	private TeamChallengeParticipationRepository teamChallengeParticipationRepository;
	@Mock
	private PersonalChallengeCertificationRepository personalChallengeCertificationRepository;
	@Mock
	private TeamChallengeCertificationRepository teamChallengeCertificationRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private TimeUtils timeUtils;

	private Member testMember;
	private PersonalChallenge testPersonalChallenge;
	private TeamChallenge testTeamChallenge;
	private PersonalChallengeParticipation testPersonalParticipation;
	private TeamChallengeParticipation testTeamParticipation;
	private PrincipalDetails principalDetails;
	private ChallengeCertificationCreateRequestDto testRequest;
	private LocalDateTime testNow;

	private static final Long TEST_MEMBER_ID = 1L;
	private static final Long TEST_CHALLENGE_ID = 1L;
	private static final String TEST_IMAGE_URL = "https://example.com/image.jpg";
	private static final String TEST_REVIEW = "오늘도 열심히 운동했습니다!";

	@BeforeEach
	void setUp() {
		testNow = LocalDateTime.of(2024, 1, 15, 14, 30);

		// 테스트 멤버 생성
		testMember = Member.create("test@example.com", "테스트사용자", "test@example.com");
		ReflectionTestUtils.setField(testMember, "id", TEST_MEMBER_ID);

		// 테스트 챌린지 생성
		testPersonalChallenge = PersonalChallenge.create(
			"PC001",
			"개인 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(1000)),
			testNow.minusDays(1),
			testNow.plusDays(30),
			"challenge.jpg",
			"챌린지 설명"
		);
		ReflectionTestUtils.setField(testPersonalChallenge, "id", TEST_CHALLENGE_ID);

		testTeamChallenge = TeamChallenge.create(
			"TC001",
			"팀 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(2000)),
			testNow.minusDays(1),
			testNow.plusDays(30),
			5,
			"team-challenge.jpg",
			"팀 챌린지 설명"
		);
		ReflectionTestUtils.setField(testTeamChallenge, "id", TEST_CHALLENGE_ID + 1);

		// 테스트 참여 정보 생성
		testPersonalParticipation = PersonalChallengeParticipation.create(
			testPersonalChallenge,
			testMember,
			testNow.minusHours(1)
		);

		testTeamParticipation = TeamChallengeParticipation.create(
			testTeamChallenge,
			testMember,
			testNow.minusHours(1)
		);

		// PrincipalDetails 생성
		principalDetails = new PrincipalDetails(TEST_MEMBER_ID, "test@example.com", "ROLE_USER", "테스트사용자",
			"test@example.com");

		// 테스트 요청 DTO 생성
		testRequest = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 15))
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(TEST_REVIEW)
			.build();

		// timeUtils mock 설정을 제거 - 각 테스트에서 필요시 설정
	}

	@Test
	void 개인_챌린지_인증을_성공적으로_생성한다() {
		// given
		Long expectedCertificationId = 100L;
		PersonalChallengeCertification mockCertification = mock(PersonalChallengeCertification.class);

		given(memberRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.of(testMember));
		given(personalChallengeRepository.findById(TEST_CHALLENGE_ID)).willReturn(Optional.of(testPersonalChallenge));
		// 개인 챌린지가 존재하므로 teamChallengeRepository는 호출되지 않음
		given(personalChallengeParticipationRepository.findByMemberAndPersonalChallenge(testMember,
			testPersonalChallenge))
			.willReturn(Optional.of(testPersonalParticipation));
		given(personalChallengeCertificationRepository.existsByParticipationAndCertifiedDate(testPersonalParticipation,
			LocalDate.of(2024, 1, 15)))
			.willReturn(false);
		given(personalChallengeCertificationRepository.save(any(PersonalChallengeCertification.class)))
			.willReturn(mockCertification);
		given(mockCertification.getId()).willReturn(expectedCertificationId);
		given(timeUtils.now()).willReturn(testNow);

		// when
		ChallengeCertificationCreateResponseDto result = challengeCertificationService.createCertification(
			TEST_CHALLENGE_ID, testRequest, principalDetails
		);

		// then
		assertThat(result.certificationId()).isEqualTo(expectedCertificationId);

		then(memberRepository).should().findById(TEST_MEMBER_ID);
		then(personalChallengeParticipationRepository).should()
			.findByMemberAndPersonalChallenge(testMember, testPersonalChallenge);
		then(personalChallengeCertificationRepository).should()
			.existsByParticipationAndCertifiedDate(testPersonalParticipation, LocalDate.of(2024, 1, 15));
		then(personalChallengeCertificationRepository).should().save(any(PersonalChallengeCertification.class));
	}

	@Test
	void 팀_챌린지_인증을_성공적으로_생성한다() {
		// given
		Long challengeId = TEST_CHALLENGE_ID + 1;
		Long expectedCertificationId = 200L;
		TeamChallengeCertification mockCertification = mock(TeamChallengeCertification.class);

		given(memberRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.of(testMember));
		given(personalChallengeRepository.findById(challengeId)).willReturn(Optional.empty());
		given(teamChallengeRepository.findById(challengeId)).willReturn(Optional.of(testTeamChallenge));
		given(teamChallengeParticipationRepository.findByMemberAndTeamChallenge(testMember, testTeamChallenge))
			.willReturn(Optional.of(testTeamParticipation));
		given(teamChallengeCertificationRepository.existsByParticipationAndCertifiedDate(testTeamParticipation,
			LocalDate.of(2024, 1, 15)))
			.willReturn(false);
		given(teamChallengeCertificationRepository.save(any(TeamChallengeCertification.class)))
			.willReturn(mockCertification);
		given(mockCertification.getId()).willReturn(expectedCertificationId);
		given(timeUtils.now()).willReturn(testNow);

		// when
		ChallengeCertificationCreateResponseDto result = challengeCertificationService.createCertification(
			challengeId, testRequest, principalDetails
		);

		// then
		assertThat(result.certificationId()).isEqualTo(expectedCertificationId);

		then(teamChallengeParticipationRepository).should()
			.findByMemberAndTeamChallenge(testMember, testTeamChallenge);
		then(teamChallengeCertificationRepository).should()
			.existsByParticipationAndCertifiedDate(testTeamParticipation, LocalDate.of(2024, 1, 15));
		then(teamChallengeCertificationRepository).should().save(any(TeamChallengeCertification.class));
	}

	@Test
	void 존재하지_않는_챌린지_ID로_인증_시도시_예외가_발생한다() {
		// given
		Long nonExistentChallengeId = 999L;

		given(memberRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.of(testMember));
		given(personalChallengeRepository.findById(nonExistentChallengeId)).willReturn(Optional.empty());
		given(teamChallengeRepository.findById(nonExistentChallengeId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> challengeCertificationService.createCertification(
			nonExistentChallengeId, testRequest, principalDetails
		))
			.isInstanceOf(ChallengeException.class)
			.hasMessage(ChallengeExceptionMessage.CHALLENGE_NOT_FOUND.getMessage());
	}

	@Test
	void 존재하지_않는_회원_ID로_인증_시도시_예외가_발생한다() {
		// given
		given(memberRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> challengeCertificationService.createCertification(
			TEST_CHALLENGE_ID, testRequest, principalDetails
		))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage());
	}

	@Test
	void 참여하지_않은_챌린지에_인증_시도시_예외가_발생한다() {
		// given
		given(memberRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.of(testMember));
		given(personalChallengeRepository.findById(TEST_CHALLENGE_ID)).willReturn(Optional.of(testPersonalChallenge));
		// 개인 챌린지가 존재하므로 teamChallengeRepository는 호출되지 않음
		given(personalChallengeParticipationRepository.findByMemberAndPersonalChallenge(testMember,
			testPersonalChallenge))
			.willReturn(Optional.empty());
		given(timeUtils.now()).willReturn(testNow);

		// when & then
		assertThatThrownBy(() -> challengeCertificationService.createCertification(
			TEST_CHALLENGE_ID, testRequest, principalDetails
		))
			.isInstanceOf(ChallengeException.class)
			.hasMessage(ChallengeExceptionMessage.NOT_PARTICIPATING.getMessage());
	}

	@Test
	void 이미_인증한_날짜에_중복_인증_시도시_예외가_발생한다() {
		// given
		given(memberRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.of(testMember));
		given(personalChallengeRepository.findById(TEST_CHALLENGE_ID)).willReturn(Optional.of(testPersonalChallenge));
		// 개인 챌린지가 존재하므로 teamChallengeRepository는 호출되지 않음
		given(personalChallengeParticipationRepository.findByMemberAndPersonalChallenge(testMember,
			testPersonalChallenge))
			.willReturn(Optional.of(testPersonalParticipation));
		given(personalChallengeCertificationRepository.existsByParticipationAndCertifiedDate(testPersonalParticipation,
			LocalDate.of(2024, 1, 15)))
			.willReturn(true);
		given(timeUtils.now()).willReturn(testNow);

		// when & then
		assertThatThrownBy(() -> challengeCertificationService.createCertification(
			TEST_CHALLENGE_ID, testRequest, principalDetails
		))
			.isInstanceOf(ChallengeCertException.class)
			.hasMessage(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_EXISTS.getMessage());
	}

	@Test
	void 미래_날짜로_인증_시도시_예외가_발생한다() {
		// given
		ChallengeCertificationCreateRequestDto futureRequest = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 16)) // 현재 시간(2024-01-15)보다 미래
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(TEST_REVIEW)
			.build();

		given(memberRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.of(testMember));
		given(personalChallengeRepository.findById(TEST_CHALLENGE_ID)).willReturn(Optional.of(testPersonalChallenge));
		// validateCertificationDate에서 바로 예외가 발생하므로 다른 repository 호출은 없음
		given(timeUtils.now()).willReturn(testNow);

		// when & then
		assertThatThrownBy(() -> challengeCertificationService.createCertification(
			TEST_CHALLENGE_ID, futureRequest, principalDetails
		))
			.isInstanceOf(ChallengeCertException.class)
			.hasMessage(ChallengeCertExceptionMessage.INVALID_CERTIFICATION_DATE.getMessage());
	}

	@Test
	void 과거_날짜로_인증_생성이_성공한다() {
		// given
		ChallengeCertificationCreateRequestDto pastRequest = ChallengeCertificationCreateRequestDto.builder()
			.certificationDate(LocalDate.of(2024, 1, 10)) // 현재 시간보다 과거
			.certificationImageUrl(TEST_IMAGE_URL)
			.certificationReview(TEST_REVIEW)
			.build();

		Long expectedCertificationId = 300L;
		PersonalChallengeCertification mockCertification = mock(PersonalChallengeCertification.class);

		given(memberRepository.findById(TEST_MEMBER_ID)).willReturn(Optional.of(testMember));
		given(personalChallengeRepository.findById(TEST_CHALLENGE_ID)).willReturn(Optional.of(testPersonalChallenge));
		// 개인 챌린지가 존재하므로 teamChallengeRepository는 호출되지 않음
		given(personalChallengeParticipationRepository.findByMemberAndPersonalChallenge(testMember,
			testPersonalChallenge))
			.willReturn(Optional.of(testPersonalParticipation));
		given(personalChallengeCertificationRepository.existsByParticipationAndCertifiedDate(testPersonalParticipation,
			LocalDate.of(2024, 1, 10)))
			.willReturn(false);
		given(personalChallengeCertificationRepository.save(any(PersonalChallengeCertification.class)))
			.willReturn(mockCertification);
		given(mockCertification.getId()).willReturn(expectedCertificationId);
		given(timeUtils.now()).willReturn(testNow);

		// when
		ChallengeCertificationCreateResponseDto result = challengeCertificationService.createCertification(
			TEST_CHALLENGE_ID, pastRequest, principalDetails
		);

		// then
		assertThat(result.certificationId()).isEqualTo(expectedCertificationId);
	}
} 
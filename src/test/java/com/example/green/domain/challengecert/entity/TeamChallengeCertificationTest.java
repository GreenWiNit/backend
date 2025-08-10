package com.example.green.domain.challengecert.entity;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.entity.vo.GroupAddress;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.domain.challengecert.enums.CertificationStatus;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.point.entity.vo.PointAmount;

class TeamChallengeCertificationTest {

	private TeamChallengeCertification certification;
	private TeamChallengeParticipation participation;
	private TeamChallengeGroup teamChallengeGroup;
	private TeamChallenge teamChallenge;
	private Member member;
	private Member participant;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();

		// 테스트용 Member 객체 생성
		member = Member.create("google 123456789", "테스트유저", "test@example.com");

		// 테스트용 TeamChallenge 생성
		teamChallenge = TeamChallenge.create(
			CodeGenerator.generate(ChallengeType.TEAM, now),
			"팀 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(2000)),
			now.minusDays(1),
			now.plusDays(7),
			5,
			"challenge-image.jpg",
			"팀 챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		// 테스트용 TeamChallengeGroup 생성
		teamChallengeGroup = TeamChallengeGroup.create(
			teamChallenge.getChallengeCode(),
			teamChallenge.getChallengeName(),
			now.minusHours(2),
			now.plusDays(6),
			10,
			GroupAddress.of("서울시 강남구"),
			"테스트 그룹 설명",
			"https://openchat.example.com",
			teamChallenge
		);

		// 테스트용 TeamChallengeParticipation 생성 (멤버로 참여)
		participation = TeamChallengeParticipation.create(
			teamChallenge,
			1L,
			now.minusHours(1)
		);

		// 테스트용 TeamChallengeCertification 생성
		certification = TeamChallengeCertification.create(
			participation,
			participant,
			"https://example.com/team-cert-image.jpg",
			"팀 챌린지 인증 후기입니다.",
			now.minusMinutes(30),
			LocalDate.of(2025, 1, 9)
		);
	}

	@Test
	void create_메서드로_팀_챌린지_인증을_생성할_수_있다() {
		// given
		String imageUrl = "https://example.com/new-team-cert.jpg";
		String review = "새로운 팀 인증 후기";
		LocalDateTime certifiedAt = now.minusMinutes(15);
		LocalDate certifiedDate = LocalDate.of(2025, 1, 10);

		// when
		TeamChallengeCertification newCertification = TeamChallengeCertification.create(
			participation,
			member,
			imageUrl,
			review,
			certifiedAt,
			certifiedDate
		);

		// then
		assertThat(newCertification.getParticipation()).isEqualTo(participation);
		assertThat(newCertification.getMember()).isEqualTo(member);
		assertThat(newCertification.getCertificationImageUrl()).isEqualTo(imageUrl);
		assertThat(newCertification.getCertificationReview()).isEqualTo(review);
		assertThat(newCertification.getCertifiedAt()).isEqualTo(certifiedAt);
		assertThat(newCertification.getCertifiedDate()).isEqualTo(certifiedDate);
		assertThat(newCertification.getStatus()).isEqualTo(CertificationStatus.PENDING);
	}
}

package com.example.green.domain.challengecert.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.enums.GroupStatus;
import com.example.green.domain.challenge.utils.ChallengeCodeGenerator;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.domain.challengecert.exception.ChallengeCertException;
import com.example.green.domain.challengecert.exception.ChallengeCertExceptionMessage;

/**
 * TeamChallengeCertification 테스트
 */
class TeamChallengeCertificationTest {

	private TeamChallengeCertification certification;
	private TeamChallengeParticipation participation;
	private TeamChallengeGroup teamChallengeGroup;
	private TeamChallenge teamChallenge;
	private Member member;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();

		// 테스트용 Member 객체 생성
		member = Member.create("google 123456789", "테스트유저", "test@example.com");

		// 테스트용 TeamChallenge 생성
		teamChallenge = TeamChallenge.create(
			ChallengeCodeGenerator.generate(ChallengeType.TEAM, now),
			"팀 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(2000)),
			now.minusDays(1),
			now.plusDays(7),
			5,
			"challenge-image.jpg",
			"팀 챌린지 설명"
		);

		// 테스트용 TeamChallengeGroup 생성
		teamChallengeGroup = TeamChallengeGroup.create(
			"테스트 그룹",
			GroupStatus.PROCEEDING,
			now.minusHours(2),
			now.plusDays(6),
			10,
			"서울시 강남구",
			"테스트 그룹 설명",
			"https://openchat.example.com"
		);

		// 팀 챌린지에 그룹 추가
		teamChallenge.addChallengeGroup(teamChallengeGroup);

		// 테스트용 TeamChallengeParticipation 생성 (멤버로 참여)
		participation = TeamChallengeParticipation.createMember(
			teamChallengeGroup,
			member,
			now.minusHours(1)
		);

		// 테스트용 TeamChallengeCertification 생성
		certification = TeamChallengeCertification.create(
			participation,
			"https://example.com/team-cert-image.jpg",
			"팀 챌린지 인증 후기입니다.",
			now.minusMinutes(30),
			"2025-01-09"
		);
	}

	@Test
	void create_메서드로_팀_챌린지_인증을_생성할_수_있다() {
		// given
		String imageUrl = "https://example.com/new-team-cert.jpg";
		String review = "새로운 팀 인증 후기";
		LocalDateTime certifiedAt = now.minusMinutes(15);
		String certifiedDate = "2025-01-10";

		// when
		TeamChallengeCertification newCertification = TeamChallengeCertification.create(
			participation,
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
		assertThat(newCertification.getApproved()).isFalse();
		assertThat(newCertification.getApprovedAt()).isNull();
	}

	@Test
	void 참여_정보가_null이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeCertification.create(
			null,
			"https://example.com/image.jpg",
			"후기",
			now,
			"2025-01-09"
		))
			.isInstanceOf(BusinessException.class);

	}

	@Test
	void 인증_이미지_URL이_null이거나_빈_문자열이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeCertification.create(
			participation,
			null,
			"후기",
			now,
			"2025-01-09"
		))
			.isInstanceOf(BusinessException.class);

		assertThatThrownBy(() -> TeamChallengeCertification.create(
			participation,
			"",
			"후기",
			now,
			"2025-01-09"
		))
			.isInstanceOf(BusinessException.class);

	}

	@Test
	void 인증_시각이_null이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeCertification.create(
			participation,
			"https://example.com/image.jpg",
			"후기",
			null,
			"2025-01-09"
		))
			.isInstanceOf(BusinessException.class);

	}

	@Test
	void 인증_날짜가_null이거나_빈_문자열이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> TeamChallengeCertification.create(
			participation,
			"https://example.com/image.jpg",
			"후기",
			now,
			null
		))
			.isInstanceOf(BusinessException.class);

		assertThatThrownBy(() -> TeamChallengeCertification.create(
			participation,
			"https://example.com/image.jpg",
			"후기",
			now,
			""
		))
			.isInstanceOf(BusinessException.class);

	}

	@Test
	void 인증_후기는_null이어도_된다() {
		// when & then
		assertDoesNotThrow(() -> TeamChallengeCertification.create(
			participation,
			"https://example.com/image.jpg",
			null,
			now,
			"2025-01-09"
		));
	}

	@Test
	void 인증을_승인할_수_있다() {
		// given
		LocalDateTime approveTime = now;

		// when
		certification.approve(approveTime);

		// then
		assertThat(certification.getApproved()).isTrue();
		assertThat(certification.getApprovedAt()).isEqualTo(approveTime);
		assertThat(certification.canApprove()).isFalse();
		assertThat(certification.canUpdate()).isFalse();
	}

	@Test
	void 미승인_상태에서_인증_내용을_수정할_수_있다() {
		// given
		String newImageUrl = "https://example.com/updated-team-image.jpg";
		String newReview = "수정된 팀 후기입니다.";

		// when
		certification.updateCertification(newImageUrl, newReview);

		// then
		assertThat(certification.getCertificationImageUrl()).isEqualTo(newImageUrl);
		assertThat(certification.getCertificationReview()).isEqualTo(newReview);
	}

	@Test
	void 이미_승인된_인증을_다시_승인하면_예외가_발생한다() {
		// given
		certification.approve(now);

		// when & then
		assertThatThrownBy(() -> certification.approve(now.plusMinutes(1)))
			.isInstanceOf(ChallengeCertException.class)
			.hasMessage(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_APPROVED.getMessage());
	}

	@Test
	void 승인_시각이_null이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> certification.approve(null))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 이미_승인된_인증을_수정하면_예외가_발생한다() {
		// given
		certification.approve(now);

		// when & then
		assertThatThrownBy(() -> certification.updateCertification(
			"https://example.com/new-team-image.jpg",
			"수정된 팀 후기"
		))
			.isInstanceOf(ChallengeCertException.class)
			.hasMessage(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_APPROVED.getMessage());
	}
}

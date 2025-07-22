package com.example.green.domain.challengecert.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.utils.ChallengeCodeGenerator;
import com.example.green.domain.challengecert.exception.ChallengeCertException;
import com.example.green.domain.challengecert.exception.ChallengeCertExceptionMessage;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.error.exception.BusinessException;

/**
 * PersonalChallengeCertification 테스트
 */
class PersonalChallengeCertificationTest {

	private PersonalChallengeCertification certification;
	private PersonalChallengeParticipation participation;
	private PersonalChallenge personalChallenge;
	private Member member;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();

		// 테스트용 Member 객체 생성
		member = Member.create("test", "테스트유저", "test@example.com");

		// 테스트용 PersonalChallenge 생성
		personalChallenge = PersonalChallenge.create(
			ChallengeCodeGenerator.generate(ChallengeType.PERSONAL, now),
			"개인 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(1000)),
			now.minusDays(1),
			now.plusDays(7),
			"challenge-image.jpg",
			"챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		// 테스트용 PersonalChallengeParticipation 생성
		participation = PersonalChallengeParticipation.create(
			personalChallenge,
			member,
			now.minusHours(2)
		);

		// 테스트용 PersonalChallengeCertification 생성
		certification = PersonalChallengeCertification.create(
			participation,
			"https://example.com/cert-image.jpg",
			"인증 후기입니다.",
			now.minusHours(1),
			LocalDate.of(2025, 1, 9)
		);
	}

	@Test
	void create_메서드로_개인_챌린지_인증을_생성할_수_있다() {
		// given
		String imageUrl = "https://example.com/new-cert.jpg";
		String review = "새로운 인증 후기";
		LocalDateTime certifiedAt = now.minusMinutes(30);
		LocalDate certifiedDate = LocalDate.of(2025, 1, 10);

		// when
		PersonalChallengeCertification newCertification = PersonalChallengeCertification.create(
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
		assertThatThrownBy(() -> PersonalChallengeCertification.create(
			null,
			"https://example.com/image.jpg",
			"후기",
			now,
			LocalDate.of(2025, 1, 9)
		))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 인증_이미지_URL이_null이거나_빈_문자열이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> PersonalChallengeCertification.create(
			participation,
			null,
			"후기",
			now,
			LocalDate.of(2025, 1, 9)
		))
			.isInstanceOf(BusinessException.class);

		assertThatThrownBy(() -> PersonalChallengeCertification.create(
			participation,
			"",
			"후기",
			now,
			LocalDate.of(2025, 1, 9)
		))
			.isInstanceOf(BusinessException.class);

	}

	@Test
	void 인증_시각이_null이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> PersonalChallengeCertification.create(
			participation,
			"https://example.com/image.jpg",
			"후기",
			null,
			LocalDate.of(2025, 1, 9)
		))
			.isInstanceOf(BusinessException.class);

	}

	@Test
	void 인증_날짜가_null이거나_빈_문자열이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> PersonalChallengeCertification.create(
			participation,
			"https://example.com/image.jpg",
			"후기",
			now,
			null
		))
			.isInstanceOf(BusinessException.class);

		assertThatThrownBy(() -> PersonalChallengeCertification.create(
			participation,
			"https://example.com/image.jpg",
			"후기",
			now,
			null
		))
			.isInstanceOf(BusinessException.class);

	}

	@Test
	void 인증_후기는_null이어도_된다() {
		// when & then
		assertDoesNotThrow(() -> PersonalChallengeCertification.create(
			participation,
			"https://example.com/image.jpg",
			null,
			now,
			LocalDate.of(2025, 1, 9)
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
		String newImageUrl = "https://example.com/updated-image.jpg";
		String newReview = "수정된 후기입니다.";

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
			"https://example.com/new-image.jpg",
			"수정된 후기"
		))
			.isInstanceOf(ChallengeCertException.class)
			.hasMessage(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_APPROVED.getMessage());
	}
}

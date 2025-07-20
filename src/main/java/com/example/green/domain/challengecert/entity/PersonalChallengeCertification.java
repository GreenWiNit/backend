package com.example.green.domain.challengecert.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 개인 챌린지 인증 엔티티
 */
@Entity
@Table(
	indexes = {
		@Index(name = "idx_personal_cert_participation", columnList = "participation_id"),
		@Index(name = "idx_personal_cert_date", columnList = "certified_at"),
		@Index(name = "idx_personal_cert_member", columnList = "member_id")
	},
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_personal_cert_once_per_day",
			columnNames = {"participation_id", "certified_date"})
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalChallengeCertification extends BaseChallengeCertification {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "participation_id", nullable = false)
	private PersonalChallengeParticipation participation;

	public static PersonalChallengeCertification create(
		PersonalChallengeParticipation participation,
		String certificationImageUrl,
		String certificationReview,
		LocalDateTime certifiedAt,
		LocalDate certifiedDate
	) {
		// 필수 값 validate
		validateNullData(participation, "챌린지 참여 정보는 필수값입니다.");
		validateEmptyString(certificationImageUrl, "인증 이미지는 필수값입니다.");
		validateNullData(certifiedAt, "인증 시각은 필수값입니다.");
		validateNullData(certifiedDate, "인증 날짜는 필수값입니다.");

		return new PersonalChallengeCertification(
			participation,
			certificationImageUrl,
			certificationReview,
			certifiedAt,
			certifiedDate
		);
	}

	private PersonalChallengeCertification(
		PersonalChallengeParticipation participation,
		String certificationImageUrl,
		String certificationReview,
		LocalDateTime certifiedAt,
		LocalDate certifiedDate
	) {
		super(participation.getMember(), certificationImageUrl, certificationReview, certifiedAt, certifiedDate);
		this.participation = participation;
		validateCertificationData();
	}
}

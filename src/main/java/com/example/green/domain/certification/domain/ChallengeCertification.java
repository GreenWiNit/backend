package com.example.green.domain.certification.domain;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDate;

import com.example.green.domain.certification.exception.CertificationException;
import com.example.green.domain.certification.exception.CertificationExceptionMessage;
import com.example.green.domain.common.TimeBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "challenge_certifications"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ChallengeCertification extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private MemberSnapshot member;
	private ChallengeSnapshot challenge;

	@Column(length = 500, nullable = false)
	private String imageUrl;

	@Column(length = 45, nullable = false)
	private String review;

	@Column(nullable = false)
	private LocalDate certifiedDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private CertificationStatus status = CertificationStatus.PENDING;

	public static ChallengeCertification create(
		MemberSnapshot member, ChallengeSnapshot challenge, String imageUrl, String review, LocalDate certifiedDate
	) {
		validateCreateInputs(member, challenge, imageUrl, review, certifiedDate);
		if (certifiedDate.isAfter(LocalDate.now())) {
			throw new CertificationException(CertificationExceptionMessage.FUTURE_DATE_NOT_ALLOWED);
		}

		return ChallengeCertification.builder()
			.member(member)
			.challenge(challenge)
			.imageUrl(imageUrl)
			.review(review)
			.certifiedDate(certifiedDate)
			.build();
	}

	private static void validateCreateInputs(
		MemberSnapshot member, ChallengeSnapshot challenge, String imageUrl, String review, LocalDate certifiedDate
	) {
		validateNullData(member, "회원 정보는 필수값입니다.");
		validateNullData(challenge, "챌린지 정보는 필수값입니다.");
		validateNullData(review, "리뷰는 NULL 만 아니면 됩니다.");
		validateEmptyString(imageUrl, "인증 이미지는 필수값입니다.");
		validateNullData(certifiedDate, "인증 시각은 필수값입니다.");
	}

	public void approve() {
		this.status = CertificationStatus.APPROVED;
	}

	public void reject() {
		this.status = CertificationStatus.REJECTED;
	}
}

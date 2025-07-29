package com.example.green.domain.challengecert.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.green.domain.challengecert.enums.CertificationStatus;
import com.example.green.domain.challengecert.exception.ChallengeCertException;
import com.example.green.domain.challengecert.exception.ChallengeCertExceptionMessage;
import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class BaseChallengeCertification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(length = 500, nullable = false)
	private String certificationImageUrl;

	@Column(columnDefinition = "TEXT")
	private String certificationReview;

	@Column(nullable = false)
	private LocalDateTime certifiedAt;

	/**
	 * 인증 날짜
	 * 하루 한 번 인증 제약조건을 위한 날짜 값
	 */
	@Column(nullable = false)
	private LocalDate certifiedDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CertificationStatus status = CertificationStatus.PENDING;

	// 하위 클래스를 위한 protected 생성자
	protected BaseChallengeCertification(
		Member member,
		String certificationImageUrl,
		String certificationReview,
		LocalDateTime certifiedAt,
		LocalDate certifiedDate
	) {
		this.member = member;
		this.certificationImageUrl = certificationImageUrl;
		this.certificationReview = certificationReview;
		this.certifiedAt = certifiedAt;
		this.certifiedDate = certifiedDate;
		this.status = CertificationStatus.PENDING;
	}

	/**
	 * 관리자 승인 처리
	 */
	public void approve() {
		if (this.status != CertificationStatus.PENDING) {
			throw new ChallengeCertException(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_PROCESSED);
		}
		this.status = CertificationStatus.PAID;
	}

	/**
	 * 관리자 거절 처리
	 */
	public void reject() {
		if (this.status != CertificationStatus.PENDING) {
			throw new ChallengeCertException(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_PROCESSED);
		}
		this.status = CertificationStatus.REJECTED;
	}

	/**
	 * 인증 내용 수정
	 */
	public void updateCertification(String certificationImageUrl, String certificationReview) {
		if (this.status != CertificationStatus.PENDING) {
			throw new ChallengeCertException(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_PROCESSED);
		}

		validateEmptyString(certificationImageUrl, "인증 이미지는 필수값입니다.");
		this.certificationImageUrl = certificationImageUrl;
		this.certificationReview = certificationReview;
	}

	/**
	 * 승인 가능 여부 확인
	 */
	public boolean canApprove() {
		return this.status == CertificationStatus.PENDING;
	}

	/**
	 * 거절 가능 여부 확인
	 */
	public boolean canReject() {
		return this.status == CertificationStatus.PENDING;
	}

	/**
	 * 수정 가능 여부 확인
	 */
	public boolean canUpdate() {
		return this.status == CertificationStatus.PENDING;
	}

	/**
	 * 승인된 상태인지 확인
	 */
	public boolean isApproved() {
		return this.status == CertificationStatus.PAID;
	}

	/**
	 * 거절된 상태인지 확인
	 */
	public boolean isRejected() {
		return this.status == CertificationStatus.REJECTED;
	}

	protected void validateCertificationData() {
		validateNullData(member, "회원은 필수값입니다.");
		validateEmptyString(certificationImageUrl, "인증 이미지는 필수값입니다.");
		validateNullData(certifiedAt, "인증 시각은 필수값입니다.");
		validateNullData(certifiedDate, "인증 날짜는 필수값입니다.");
	}
}

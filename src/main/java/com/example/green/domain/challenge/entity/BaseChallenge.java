package com.example.green.domain.challenge.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.pointshop.entity.point.vo.PointAmount;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 챌린지 공통 필드를 담은 기본 클래스
 */
@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class BaseChallenge extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 90, nullable = false)
	private String challengeName;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private ChallengeStatus challengeStatus;

	@Embedded
	@AttributeOverride(name = "amount", column = @Column(name = "challenge_point"))
	private PointAmount challengePoint;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private ChallengeType challengeType;

	@Column(length = 100)
	private String challengeImage;

	@Column(columnDefinition = "TEXT")
	private String challengeContent;

	@Column(nullable = false)
	private LocalDateTime beginDateTime;

	@Column(nullable = false)
	private LocalDateTime endDateTime;

	// 하위 클래스를 위한 protected 생성자
	protected BaseChallenge(
		String challengeName,
		ChallengeStatus challengeStatus,
		PointAmount challengePoint,
		ChallengeType challengeType,
		LocalDateTime beginDateTime,
		LocalDateTime endDateTime,
		String challengeImage,
		String challengeContent
	) {
		this.challengeName = challengeName;
		this.challengeStatus = challengeStatus;
		this.challengePoint = challengePoint;
		this.challengeType = challengeType;
		this.beginDateTime = beginDateTime;
		this.endDateTime = endDateTime;
		this.challengeImage = challengeImage;
		this.challengeContent = challengeContent;
	}

	/**
	 * 챌린지가 활성 상태인지 확인
	 */
	public boolean isActive() {
		LocalDateTime now = LocalDateTime.now();
		return challengeStatus == ChallengeStatus.PROCEEDING
			&& now.isAfter(beginDateTime)
			&& now.isBefore(endDateTime);
	}

	/**
	 * 챌린지 참여 가능 여부 확인
	 */
	public boolean canParticipate() {
		return challengeStatus == ChallengeStatus.PROCEEDING && LocalDateTime.now().isBefore(endDateTime);
	}

	/**
	 * 상태 변경 (도메인 서비스에서만 호출)
	 */
	protected void updateStatus(ChallengeStatus newStatus) {
		validateNullData(newStatus, "챌린지 상태는 필수값입니다.");
		this.challengeStatus = newStatus;
	}

	protected void validateChallengeData() {
		validateEmptyString(challengeName, "챌린지명은 필수값입니다.");
		validateStringLength(challengeName, 2, 90, "챌린지명은 1-30자 사이여야 합니다.");
		validateNullData(challengeStatus, "챌린지 상태는 필수값입니다.");
		validateNullData(challengeType, "챌린지 유형은 필수값입니다.");
		validateNullData(challengePoint, "챌린지 포인트는 필수값입니다.");
		validateDateRange(beginDateTime, endDateTime, "시작일시는 종료일시보다 이전이어야 합니다.");
	}
}

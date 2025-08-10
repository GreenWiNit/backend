package com.example.green.domain.challenge.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseChallenge extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "challenge_code", length = 30, nullable = false)
	private String challengeCode;

	@Column(length = 90, nullable = false)
	private String challengeName;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal challengePoint;

	@Column(length = 100)
	private String challengeImage;

	@Column(columnDefinition = "TEXT")
	private String challengeContent;

	@Column(nullable = false)
	private LocalDateTime beginDateTime;

	@Column(nullable = false)
	private LocalDateTime endDateTime;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private ChallengeType challengeType;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private ChallengeStatus challengeStatus;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private ChallengeDisplayStatus displayStatus;

	protected BaseChallenge(
		String challengeCode,
		String challengeName,
		String challengeImage,
		String challengeContent,
		BigDecimal challengePoint,
		LocalDateTime beginDateTime,
		LocalDateTime endDateTime,
		ChallengeType challengeType
	) {
		if (challengePoint.compareTo(BigDecimal.ZERO) < 0) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_MINIMUM_POINT);
		}
		validateCreateParameters(challengeCode, challengeName, challengeImage, challengeContent,
			beginDateTime, endDateTime, challengeType);
		this.challengeCode = challengeCode;
		this.challengeName = challengeName;
		this.challengeImage = challengeImage;
		this.challengeContent = challengeContent;
		this.challengePoint = challengePoint;
		this.beginDateTime = beginDateTime;
		this.endDateTime = endDateTime;
		this.challengeType = challengeType;
		this.challengeStatus = ChallengeStatus.PROCEEDING;
		this.displayStatus = ChallengeDisplayStatus.VISIBLE;
	}

	private static void validateCreateParameters(
		String challengeCode,
		String challengeName,
		String challengeImage,
		String challengeContent,
		LocalDateTime beginDateTime,
		LocalDateTime endDateTime,
		ChallengeType challengeType
	) {
		validateEmptyString(challengeCode, "챌린지 코드는 필수값입니다.");
		validateEmptyString(challengeName, "챌린지명은 필수값입니다.");
		validateEmptyString(challengeImage, "챌린지 이미지는 필수값입니다.");
		validateEmptyString(challengeContent, "챌린지 내용은 필수값입니다.");
		validateNullData(beginDateTime, "시작일시는 필수값입니다.");
		validateNullData(endDateTime, "종료일시는 필수값입니다.");
		validateDateRange(beginDateTime, endDateTime, "시작일시는 종료일시보다 이전이어야 합니다.");
		validateNullData(challengeType, "챌린지 타입은 필수값입니다.");
	}

	public boolean isActive(LocalDateTime now) {
		return challengeStatus == ChallengeStatus.PROCEEDING
			&& displayStatus == ChallengeDisplayStatus.VISIBLE
			&& now.isBefore(endDateTime);
	}

	public void show() {
		this.displayStatus = ChallengeDisplayStatus.VISIBLE;
	}

	public void hide() {
		this.displayStatus = ChallengeDisplayStatus.HIDDEN;
	}

	public void updateImage(String newImageUrl) {
		validateEmptyString(newImageUrl, "챌린지 이미지 URL은 필수값입니다.");
		this.challengeImage = newImageUrl;
	}

	public void updateBasicInfo(
		String challengeName,
		BigDecimal challengePoint,
		LocalDateTime beginDateTime,
		LocalDateTime endDateTime,
		String challengeContent
	) {
		validateEmptyString(challengeName, "챌린지명은 필수값입니다.");
		validateNullData(challengePoint, "챌린지 포인트는 필수값입니다.");
		validateNullData(beginDateTime, "시작일시는 필수값입니다.");
		validateNullData(endDateTime, "종료일시는 필수값입니다.");
		validateDateRange(beginDateTime, endDateTime, "시작일시는 종료일시보다 이전이어야 합니다.");

		this.challengeName = challengeName;
		this.challengePoint = challengePoint;
		this.beginDateTime = beginDateTime;
		this.endDateTime = endDateTime;
		this.challengeContent = challengeContent;
	}

	// 템플릿 메서드 패턴
	public final void addParticipation(Long memberId, LocalDateTime now) {
		validateParticipation(memberId, now);
		doAddParticipation(memberId, now);
	}

	protected final void validateParticipation(Long memberId, LocalDateTime now) {
		if (isAlreadyParticipated(memberId)) {
			throw new ChallengeException(ChallengeExceptionMessage.ALREADY_PARTICIPATING);
		}
		if (!isParticipationPeriod(now)) {
			throw new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_PARTICIPATABLE);
		}
	}

	protected final boolean isParticipationPeriod(LocalDateTime now) {
		return !now.isBefore(getBeginDateTime()) && !now.isAfter(getEndDateTime());
	}

	protected abstract boolean isAlreadyParticipated(Long memberId);

	protected abstract void doAddParticipation(Long memberId, LocalDateTime now);
}
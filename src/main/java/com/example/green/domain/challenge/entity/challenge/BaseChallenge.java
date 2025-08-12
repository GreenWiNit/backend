package com.example.green.domain.challenge.entity.challenge;

import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplayStatus;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeStatus;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
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
	private LocalDate beginDate;

	@Column(nullable = false)
	private LocalDate endDate;

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
		LocalDate beginDate,
		LocalDate endDate,
		ChallengeType challengeType
	) {
		validateCreateParameters(challengeCode, challengeName, challengeImage, challengeContent,
			beginDate, endDate, challengeType);
		if (challengePoint.compareTo(BigDecimal.ZERO) < 0) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_MINIMUM_POINT);
		}
		if (beginDate.isAfter(endDate)) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_CHALLENGE_PERIOD);
		}
		this.challengeCode = challengeCode;
		this.challengeName = challengeName;
		this.challengeImage = challengeImage;
		this.challengeContent = challengeContent;
		this.challengePoint = challengePoint;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.challengeType = challengeType;
		this.challengeStatus = ChallengeStatus.PROCEEDING;
		this.displayStatus = ChallengeDisplayStatus.VISIBLE;
	}

	private static void validateCreateParameters(
		String challengeCode,
		String challengeName,
		String challengeImage,
		String challengeContent,
		LocalDate beginDate,
		LocalDate endDate,
		ChallengeType challengeType
	) {
		validateEmptyString(challengeCode, "챌린지 코드는 필수값입니다.");
		validateEmptyString(challengeName, "챌린지명은 필수값입니다.");
		validateEmptyString(challengeImage, "챌린지 이미지는 필수값입니다.");
		validateEmptyString(challengeContent, "챌린지 내용은 필수값입니다.");
		validateNullData(beginDate, "시작일시는 필수값입니다.");
		validateNullData(endDate, "종료일시는 필수값입니다.");
		validateNullData(challengeType, "챌린지 타입은 필수값입니다.");
	}

	public boolean isActive(LocalDate now) {
		return challengeStatus == ChallengeStatus.PROCEEDING
			&& displayStatus == ChallengeDisplayStatus.VISIBLE
			&& !now.isBefore(beginDate)
			&& !now.isAfter(endDate);
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
		LocalDate beginDate,
		LocalDate endDate,
		String challengeContent
	) {
		validateEmptyString(challengeName, "챌린지명은 필수값입니다.");
		validateNullData(challengePoint, "챌린지 포인트는 필수값입니다.");
		validateNullData(beginDate, "시작일시는 필수값입니다.");
		validateNullData(endDate, "종료일시는 필수값입니다.");
		if (beginDate.isAfter(endDate)) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_CHALLENGE_PERIOD);
		}

		this.challengeName = challengeName;
		this.challengePoint = challengePoint;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.challengeContent = challengeContent;
	}

	public final void addParticipation(Long memberId, LocalDateTime now) {
		validateParticipation(memberId, now);
		doAddParticipation(memberId, now);
	}

	protected final void validateParticipation(Long memberId, LocalDateTime now) {
		if (isAlreadyParticipated(memberId)) {
			throw new ChallengeException(ChallengeExceptionMessage.ALREADY_PARTICIPATING);
		}
		if (!isActive(now.toLocalDate())) {
			throw new ChallengeException(ChallengeExceptionMessage.INACTIVE_CHALLENGE);
		}
	}

	protected abstract boolean isAlreadyParticipated(Long memberId);

	protected abstract void doAddParticipation(Long memberId, LocalDateTime now);
}
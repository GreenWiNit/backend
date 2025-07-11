package com.example.green.domain.challenge.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.point.entity.vo.PointAmount;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 개인 챌린지 엔티티
 */
@Entity
@Table(
	indexes = {
		@Index(name = "idx_personal_challenge_active", columnList = "challengeStatus, beginDateTime, endDateTime")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_personal_challenge_code", columnNames = "challenge_code")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalChallenge extends BaseChallenge {

	public static PersonalChallenge create(
		String challengeCode,
		String challengeName,
		ChallengeStatus challengeStatus,
		PointAmount challengePoint,
		LocalDateTime beginDateTime,
		LocalDateTime endDateTime,
		String challengeImage,
		String challengeContent
	) {
		// 필수 값 validate
		validateEmptyString(challengeCode, "챌린지 코드는 필수값입니다.");
		validateEmptyString(challengeName, "챌린지명은 필수값입니다.");
		validateNullData(challengeStatus, "챌린지 상태는 필수값입니다.");
		validateNullData(challengePoint, "챌린지 포인트는 필수값입니다.");
		validateNullData(beginDateTime, "시작일시는 필수값입니다.");
		validateNullData(endDateTime, "종료일시는 필수값입니다.");
		validateDateRange(beginDateTime, endDateTime, "시작일시는 종료일시보다 이전이어야 합니다.");

		return new PersonalChallenge(
			challengeCode,
			challengeName,
			challengeStatus,
			challengePoint,
			ChallengeType.PERSONAL,
			beginDateTime,
			endDateTime,
			challengeImage,
			challengeContent
		);
	}

	private PersonalChallenge(
		String challengeCode,
		String challengeName,
		ChallengeStatus challengeStatus,
		PointAmount challengePoint,
		ChallengeType challengeType,
		LocalDateTime beginDateTime,
		LocalDateTime endDateTime,
		String challengeImage,
		String challengeContent
	) {
		super(challengeCode, challengeName, challengeStatus, challengePoint, challengeType,
			beginDateTime, endDateTime, challengeImage, challengeContent);
	}
}

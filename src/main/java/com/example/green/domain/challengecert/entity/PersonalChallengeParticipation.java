package com.example.green.domain.challengecert.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.PersonalChallenge;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 개인 챌린지 참여 엔티티
 */
@Entity
@Table(
	indexes = {
		@Index(name = "idx_personal_participation_challenge", columnList = "personal_challenge_id"),
		@Index(name = "idx_personal_participation_member", columnList = "member_id")
	},
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_personal_participation_challenge_member",
			columnNames = {"personal_challenge_id", "member_id"})}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PersonalChallengeParticipation extends BaseChallengeParticipation {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "personal_challenge_id", nullable = false)
	private PersonalChallenge personalChallenge;

	public static PersonalChallengeParticipation create(
		PersonalChallenge personalChallenge,
		Long memberId,
		LocalDateTime participatedAt
	) {
		// 필수 값 validate
		validateNullData(personalChallenge, "개인 챌린지는 필수값입니다.");
		validateAutoIncrementId(memberId, "회원 정보는 필수값입니다.");
		validateNullData(participatedAt, "참여 시각은 필수값입니다.");

		return new PersonalChallengeParticipation(personalChallenge, memberId, participatedAt);
	}

	private PersonalChallengeParticipation(
		PersonalChallenge personalChallenge,
		Long memberId,
		LocalDateTime participatedAt
	) {
		super(memberId, participatedAt);
		this.personalChallenge = personalChallenge;
	}
}

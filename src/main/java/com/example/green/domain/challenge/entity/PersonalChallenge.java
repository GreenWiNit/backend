package com.example.green.domain.challenge.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
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
		@Index(name = "idx_personal_challenge_active", columnList = "challengeStatus, displayStatus, beginDateTime, endDateTime")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_personal_challenge_code", columnNames = "challenge_code")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalChallenge extends BaseChallenge {

	@OneToMany(mappedBy = "personalChallenge", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PersonalChallengeParticipation> participations = new ArrayList<>();

	private PersonalChallenge(
		String challengeCode, String challengeName, String challengeImage, String challengeContent,
		BigDecimal challengePoint, LocalDateTime beginDateTime, LocalDateTime endDateTime
	) {
		super(challengeCode, challengeName, challengeImage, challengeContent, challengePoint, beginDateTime,
			endDateTime, ChallengeType.PERSONAL);
	}

	public static PersonalChallenge create(
		String challengeCode, String challengeName, String challengeImage, String challengeContent,
		BigDecimal challengePoint, LocalDateTime beginDateTime, LocalDateTime endDateTime
	) {
		return new PersonalChallenge(
			challengeCode, challengeName, challengeImage, challengeContent, challengePoint, beginDateTime, endDateTime
		);
	}

	protected boolean isAlreadyParticipated(Long memberId) {
		return participations.stream()
			.anyMatch(p -> p.getMemberId().equals(memberId));
	}

	protected void doAddParticipation(Long memberId, LocalDateTime now) {
		PersonalChallengeParticipation participation =
			PersonalChallengeParticipation.create(this, memberId, now);
		participations.add(participation);
	}

	public void removeParticipation(Long memberId, LocalDateTime now) {
		PersonalChallengeParticipation participation = findParticipationByMemberId(memberId);
		if (!isActive(now)) {
			throw new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_LEAVEABLE);
		}

		participations.remove(participation);
	}

	private PersonalChallengeParticipation findParticipationByMemberId(Long memberId) {
		return participations.stream()
			.filter(p -> p.isParticipated(memberId))
			.findFirst()
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING));
	}
}
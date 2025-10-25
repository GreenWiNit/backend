package com.example.green.domain.challenge.entity.challenge;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 개인 챌린지 엔티티
 */
@Entity
@Table(name = "personal_challenges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalChallenge extends BaseChallenge {

	@OneToMany(mappedBy = "personalChallenge", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PersonalChallengeParticipation> participations = new ArrayList<>();

	private PersonalChallenge(
		String challengeCode, String challengeName, String challengeImage, String challengeContent,
		BigDecimal challengePoint, LocalDate beginDate, LocalDate endDate, ChallengeDisplay displayStatus
	) {
		super(challengeCode, challengeName, challengeImage, challengeContent, challengePoint, beginDate,
			endDate, ChallengeType.PERSONAL, displayStatus);
	}

	public static PersonalChallenge create(
		String challengeCode, String challengeName, String challengeImage, String challengeContent,
		BigDecimal challengePoint, LocalDate beginDate, LocalDate endDate, ChallengeDisplay displayStatus
	) {
		return new PersonalChallenge(
			challengeCode, challengeName, challengeImage, challengeContent,
			challengePoint, beginDate, endDate, displayStatus
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

	protected void doRemoveParticipation(Long memberId, LocalDateTime now) {
		PersonalChallengeParticipation participation = findParticipationByMemberId(memberId);
		participations.remove(participation);
	}

	private PersonalChallengeParticipation findParticipationByMemberId(Long memberId) {
		return participations.stream()
			.filter(p -> p.isParticipated(memberId))
			.findFirst()
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING_CHALLENGE));
	}
}
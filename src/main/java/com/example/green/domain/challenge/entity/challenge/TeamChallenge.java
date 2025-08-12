package com.example.green.domain.challenge.entity.challenge;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_challenges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamChallenge extends BaseChallenge {

	@Column(nullable = false)
	private Integer teamCount;

	@OneToMany(mappedBy = "teamChallenge", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TeamChallengeParticipation> participations = new ArrayList<>();

	private TeamChallenge(
		String challengeCode, String challengeName, String challengeImage, String challengeContent,
		BigDecimal challengePoint, LocalDate beginDate, LocalDate endDate
	) {
		super(challengeCode, challengeName, challengeImage, challengeContent, challengePoint, beginDate,
			endDate, ChallengeType.TEAM);
		this.teamCount = 0;
	}

	public static TeamChallenge create(
		String challengeCode, String challengeName, String challengeImage, String challengeContent,
		BigDecimal challengePoint, LocalDate beginDate, LocalDate endDate
	) {
		return new TeamChallenge(
			challengeCode, challengeName, challengeImage, challengeContent,
			challengePoint, beginDate, endDate
		);
	}

	protected boolean isAlreadyParticipated(Long memberId) {
		return participations.stream()
			.anyMatch(p -> p.getMemberId().equals(memberId));
	}

	protected void doAddParticipation(Long memberId, LocalDateTime now) {
		TeamChallengeParticipation participation = TeamChallengeParticipation.create(this, memberId, now);
		participations.add(participation);
	}

	public void removeParticipation(Long memberId, LocalDateTime now) {
		TeamChallengeParticipation participation = findParticipationByMemberId(memberId);
		if (!isActive(now.toLocalDate())) {
			throw new ChallengeException(ChallengeExceptionMessage.INACTIVE_CHALLENGE);
		}

		participations.remove(participation);
	}

	private TeamChallengeParticipation findParticipationByMemberId(Long memberId) {
		return participations.stream()
			.filter(p -> p.isParticipated(memberId))
			.findFirst()
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING));
	}
}
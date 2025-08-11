package com.example.green.domain.challenge.entity.challenge;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_team_challenge_member", columnNames = {"team_challenge_id", "member_id"})
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class TeamChallengeParticipation extends BaseChallengeParticipation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_challenge_id", nullable = false)
	private TeamChallenge teamChallenge;

	public static TeamChallengeParticipation create(TeamChallenge challenge, Long memberId, LocalDateTime now) {
		validateNullData(challenge, "팀 챌린지는 필수입니다.");
		validateAutoIncrementId(memberId, "회원 정보는 필수값입니다.");
		validateNullData(now, "참여 시각은 필수입니다.");

		return new TeamChallengeParticipation(challenge, memberId, now);
	}

	private TeamChallengeParticipation(TeamChallenge challenge, Long memberId, LocalDateTime participatedAt) {
		super(memberId, participatedAt);
		this.teamChallenge = challenge;
	}
}

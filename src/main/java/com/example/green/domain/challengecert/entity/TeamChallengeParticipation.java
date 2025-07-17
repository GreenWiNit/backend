package com.example.green.domain.challengecert.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
public class TeamChallengeParticipation extends BaseChallengeParticipation {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_challenge_id", nullable = false)
	private TeamChallenge teamChallenge;

	@OneToOne(mappedBy = "teamChallengeParticipation", cascade = CascadeType.ALL, orphanRemoval = true)
	private TeamChallengeGroupParticipation groupParticipation;

	public static TeamChallengeParticipation create(
		TeamChallenge challenge,
		Member member,
		LocalDateTime participatedAt
	) {
		validateNullData(challenge, "팀 챌린지는 필수입니다.");
		validateNullData(member, "회원은 필수입니다.");
		validateNullData(participatedAt, "참여 시각은 필수입니다.");

		return new TeamChallengeParticipation(challenge, member, participatedAt);
	}

	private TeamChallengeParticipation(
		TeamChallenge challenge,
		Member member,
		LocalDateTime participatedAt
	) {
		super(member, participatedAt);
		this.teamChallenge = challenge;
	}

	public boolean hasGroup() {
		return groupParticipation != null;
	}
}

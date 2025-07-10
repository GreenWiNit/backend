package com.example.green.domain.challengecert.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challengecert.entity.enums.GroupRoleType;
import com.example.green.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	indexes = {
		@Index(name = "idx_team_participation_group", columnList = "team_challenge_group_id"),
		@Index(name = "idx_team_participation_member", columnList = "member_id")
	},
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_team_participation_group_member",
			columnNames = {"team_challenge_group_id", "member_id"})
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamChallengeParticipation extends BaseChallengeParticipation {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_challenge_group_id", nullable = false)
	private TeamChallengeGroup teamChallengeGroup;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private GroupRoleType groupRoleType;

	/**
	 * 팀 리더로 참여
	 */
	public static TeamChallengeParticipation createLeader(
		TeamChallengeGroup teamChallengeGroup,
		Member member,
		LocalDateTime participatedAt
	) {
		return create(
			teamChallengeGroup,
			member,
			GroupRoleType.LEADER,
			participatedAt
		);
	}

	/**
	 * 팀원으로 참여
	 */
	public static TeamChallengeParticipation createMember(
		TeamChallengeGroup teamChallengeGroup,
		Member member,
		LocalDateTime participatedAt
	) {
		return create(
			teamChallengeGroup,
			member,
			GroupRoleType.MEMBER,
			participatedAt
		);
	}

	private static TeamChallengeParticipation create(
		TeamChallengeGroup teamChallengeGroup,
		Member member,
		GroupRoleType groupRoleType,
		LocalDateTime participatedAt
	) {
		// 필수 값 validate
		validateNullData(teamChallengeGroup, "팀 챌린지 그룹은 필수값입니다.");
		validateNullData(member, "회원은 필수값입니다.");
		validateNullData(groupRoleType, "팀 내 역할은 필수값입니다.");
		validateNullData(participatedAt, "참여 시각은 필수값입니다.");

		return new TeamChallengeParticipation(
			teamChallengeGroup,
			member,
			groupRoleType,
			participatedAt
		);
	}

	private TeamChallengeParticipation(
		TeamChallengeGroup teamChallengeGroup,
		Member member,
		GroupRoleType groupRoleType,
		LocalDateTime participatedAt
	) {
		super(member, participatedAt);
		this.teamChallengeGroup = teamChallengeGroup;
		this.groupRoleType = groupRoleType;
	}

	/**
	 * 팀 리더인지 확인
	 */
	public boolean isLeader() {
		return GroupRoleType.LEADER.equals(this.groupRoleType);
	}
}

package com.example.green.domain.challengecert.entity;

import static com.example.green.global.utils.EntityValidator.*;

import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challengecert.entity.enums.GroupRoleType;
import com.example.green.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
		@UniqueConstraint(
			name = "uk_group_participation_by_challenge",
			columnNames = {"team_challenge_participation_id"})}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class TeamChallengeGroupParticipation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_challenge_participation_id", nullable = false, unique = true)
	private TeamChallengeParticipation teamChallengeParticipation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_challenge_group_id", nullable = false)
	private TeamChallengeGroup teamChallengeGroup;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private GroupRoleType groupRoleType;

	private TeamChallengeGroupParticipation(
		TeamChallengeParticipation participation,
		TeamChallengeGroup group,
		GroupRoleType role
	) {
		this.teamChallengeParticipation = participation;
		this.teamChallengeGroup = group;
		this.groupRoleType = role;
	}

	public static TeamChallengeGroupParticipation create(
		TeamChallengeParticipation participation,
		TeamChallengeGroup group,
		GroupRoleType role
	) {
		validateNullData(participation, "참여 정보는 필수입니다.");
		validateNullData(group, "그룹은 필수입니다.");
		validateNullData(role, "역할은 필수입니다.");

		validateGroupParticipation(group);

		TeamChallengeGroupParticipation groupParticipation =
			new TeamChallengeGroupParticipation(participation, group, role);
		group.addParticipant();
		return groupParticipation;
	}

	public void changeRole(GroupRoleType newRole) {
		validateNullData(newRole, "역할은 필수입니다.");
		this.groupRoleType = newRole;
	}

	public void leave() {
		validateLeaving();
		teamChallengeGroup.removeParticipant();
	}

	private static void validateGroupParticipation(TeamChallengeGroup group) {
		if (group.isMaxParticipantsReached()) {
			throw new ChallengeException(ChallengeExceptionMessage.GROUP_IS_FULL);
		}
	}

	private void validateLeaving() {
		if (this.groupRoleType == GroupRoleType.LEADER && teamChallengeGroup.getCurrentParticipants() > 1) {
			throw new ChallengeException(ChallengeExceptionMessage.LEADER_CANNOT_LEAVE_WITH_MEMBERS);
		}
	}

	public boolean isLeader() {
		return this.groupRoleType == GroupRoleType.LEADER;
	}
}

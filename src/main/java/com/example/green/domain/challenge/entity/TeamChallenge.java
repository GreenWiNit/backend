package com.example.green.domain.challenge.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팀 챌린지 엔티티
 */
@Entity
@Table(indexes = {
	@Index(name = "idx_team_challenge_active", columnList = "challengeStatus, beginDateTime, endDateTime")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamChallenge extends BaseChallenge {

	@Column
	private Integer maxGroupCount;

	@Column
	private Integer currentGroupCount;

	@OneToMany(mappedBy = "teamChallenge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<TeamChallengeGroup> challengeGroups = new ArrayList<>();

	public void addChallengeGroup(TeamChallengeGroup group) {
		challengeGroups.add(group);
		group.setTeamChallenge(this);
		increaseGroupCount();
	}

	public void removeChallengeGroup(TeamChallengeGroup group) {
		challengeGroups.remove(group);
		group.setTeamChallenge(null);
		decreaseGroupCount();
	}

	private void increaseGroupCount() {
		if (currentGroupCount == null) {
			currentGroupCount = 0;
		}
		currentGroupCount++;
	}

	private void decreaseGroupCount() {
		if (currentGroupCount != null && currentGroupCount > 0) {
			currentGroupCount--;
		}
	}

	public boolean canAddGroup() {
		if (maxGroupCount == null) {
			return true; // 제한 없음
		}
		return currentGroupCount == null || currentGroupCount < maxGroupCount;
	}

	public boolean isMaxGroupCountReached() {
		return maxGroupCount != null && currentGroupCount != null && currentGroupCount >= maxGroupCount;
	}
}

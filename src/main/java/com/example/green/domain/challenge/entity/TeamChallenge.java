package com.example.green.domain.challenge.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.pointshop.entity.point.vo.PointAmount;

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
	@Index(name = "idx_team_challenge_active", columnList = "challengeStatus, beginDateTime, endDateTime"),
	@Index(name = "idx_team_challenge_code", columnList = "challenge_code")
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

	public static TeamChallenge create(
		String challengeName,
		ChallengeStatus challengeStatus,
		PointAmount challengePoint,
		LocalDateTime beginDateTime,
		LocalDateTime endDateTime,
		Integer maxGroupCount,
		String challengeImage,
		String challengeContent
	) {
		// 필수 값 validate
		validateEmptyString(challengeName, "챌린지명은 필수값입니다.");
		validateNullData(challengeStatus, "챌린지 상태는 필수값입니다.");
		validateNullData(challengePoint, "챌린지 포인트는 필수값입니다.");
		validateNullData(beginDateTime, "시작일시는 필수값입니다.");
		validateNullData(endDateTime, "종료일시는 필수값입니다.");
		validateDateRange(beginDateTime, endDateTime, "시작일시는 종료일시보다 이전이어야 합니다.");

		if (maxGroupCount != null && maxGroupCount <= 0) {
			throw new IllegalArgumentException("최대 그룹 수는 1 이상이어야 합니다.");
		}

		return new TeamChallenge(
			challengeName,
			challengeStatus,
			challengePoint,
			ChallengeType.TEAM,
			beginDateTime,
			endDateTime,
			challengeImage,
			challengeContent,
			maxGroupCount
		);
	}

	private TeamChallenge(
		String challengeName,
		ChallengeStatus challengeStatus,
		PointAmount challengePoint,
		ChallengeType challengeType,
		LocalDateTime beginDateTime,
		LocalDateTime endDateTime,
		String challengeImage,
		String challengeContent,
		Integer maxGroupCount
	) {
		super(challengeName, challengeStatus, challengePoint, challengeType,
			beginDateTime, endDateTime, challengeImage, challengeContent);
		this.maxGroupCount = maxGroupCount;
		this.currentGroupCount = 0;
		this.challengeGroups = new ArrayList<>();
	}

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

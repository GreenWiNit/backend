package com.example.green.domain.challenge.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.point.entity.vo.PointAmount;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팀 챌린지 엔티티
 */
@Entity
@Table(
	indexes = {
		@Index(name = "idx_team_challenge_active", columnList = "challengeStatus, beginDateTime, endDateTime")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_team_challenge_code", columnNames = "challenge_code")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamChallenge extends BaseChallenge {

	@Column
	private Integer maxGroupCount;

	@Column
	private Integer currentGroupCount;

	@OneToMany(mappedBy = "teamChallenge", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TeamChallengeGroup> challengeGroups = new ArrayList<>();

	private TeamChallenge(
		String challengeCode,
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
		super(challengeCode, challengeName, challengeStatus, challengePoint, challengeType,
			beginDateTime, endDateTime, challengeImage, challengeContent);
		this.maxGroupCount = maxGroupCount;
		this.currentGroupCount = 0;
		this.challengeGroups = new ArrayList<>();
	}

	public static TeamChallenge create(
		String challengeCode,
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
		validateEmptyString(challengeCode, "챌린지 코드는 필수값입니다.");
		validateEmptyString(challengeName, "챌린지명은 필수값입니다.");
		validateNullData(challengeStatus, "챌린지 상태는 필수값입니다.");
		validateNullData(challengePoint, "챌린지 포인트는 필수값입니다.");
		validateNullData(beginDateTime, "시작일시는 필수값입니다.");
		validateNullData(endDateTime, "종료일시는 필수값입니다.");
		validateDateRange(beginDateTime, endDateTime, "시작일시는 종료일시보다 이전이어야 합니다.");

		if (maxGroupCount != null && maxGroupCount <= 0) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_MAX_GROUP_COUNT);
		}

		return new TeamChallenge(
			challengeCode,
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

	public void addChallengeGroup(TeamChallengeGroup group) {
		if (!canAddGroup()) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_MAX_GROUP_COUNT);
		}
		challengeGroups.add(group);
		increaseGroupCount();
	}

	public void removeChallengeGroup(TeamChallengeGroup group) {
		if (challengeGroups.remove(group)) {
			group.disconnectFromTeamChallenge();
			decreaseGroupCount();
		}
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

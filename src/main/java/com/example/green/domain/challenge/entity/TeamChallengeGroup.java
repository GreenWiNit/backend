package com.example.green.domain.challenge.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.vo.GroupAddress;
import com.example.green.domain.challenge.enums.GroupStatus;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(indexes = {
	@Index(name = "idx_team_challenge_group_active", columnList = "group_begin_date_time, group_end_date_time"),
	@Index(name = "idx_team_challenge_group_by_challenge", columnList = "team_challenge_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamChallengeGroup extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "team_code", length = 30, nullable = false)
	private String teamCode;

	@Column(length = 100, nullable = false)
	private String groupName;

	@Embedded
	private GroupAddress groupAddress;

	@Column(columnDefinition = "TEXT")
	private String groupDescription;

	@Column(length = 500)
	private String openChatUrl;

	@Column(nullable = false)
	private LocalDateTime groupBeginDateTime;

	@Column(nullable = false)
	private LocalDateTime groupEndDateTime;

	@Column
	private Integer maxParticipants;

	@Column
	private Integer currentParticipants = 0;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_challenge_id")
	private TeamChallenge teamChallenge;

	private TeamChallengeGroup(
		String teamCode,
		String groupName,
		LocalDateTime groupBeginDateTime,
		LocalDateTime groupEndDateTime,
		Integer maxParticipants,
		GroupAddress groupAddress,
		String groupDescription,
		String openChatUrl,
		TeamChallenge teamChallenge
	) {
		this.teamCode = teamCode;
		this.groupName = groupName;
		this.groupBeginDateTime = groupBeginDateTime;
		this.groupEndDateTime = groupEndDateTime;
		this.maxParticipants = maxParticipants;
		this.currentParticipants = 0;
		this.groupAddress = groupAddress;
		this.groupDescription = groupDescription;
		this.openChatUrl = openChatUrl;
		this.teamChallenge = teamChallenge;
	}

	public static TeamChallengeGroup create(
		String teamCode,
		String groupName,
		LocalDateTime groupBeginDateTime,
		LocalDateTime groupEndDateTime,
		Integer maxParticipants,
		GroupAddress groupAddress,
		String groupDescription,
		String openChatUrl,
		TeamChallenge teamChallenge
	) {
		validateEmptyString(teamCode, "팀 코드는 필수값입니다.");
		validateEmptyString(groupName, "그룹명은 필수값입니다.");
		validateNullData(groupBeginDateTime, "그룹 시작일시는 필수값입니다.");
		validateNullData(groupEndDateTime, "그룹 종료일시는 필수값입니다.");
		validateDateRange(groupBeginDateTime, groupEndDateTime, "그룹 시작일시는 종료일시보다 이전이어야 합니다.");
		validateNullData(groupAddress, "그룹 주소는 필수값입니다.");
		validateNullData(teamChallenge, "팀 챌린지는 필수값입니다.");

		if (maxParticipants != null && maxParticipants <= 0) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_MAX_PARTICIPANTS_COUNT);
		}

		TeamChallengeGroup group = new TeamChallengeGroup(
			teamCode,
			groupName,
			groupBeginDateTime,
			groupEndDateTime,
			maxParticipants,
			groupAddress,
			groupDescription,
			openChatUrl,
			teamChallenge
		);
		return group;
	}

	/**
	 * 그룹 정보를 수정합니다.
	 *
	 * @param groupName 수정할 그룹명
	 * @param groupAddress 수정할 그룹 주소
	 * @param groupDescription 수정할 그룹 설명
	 * @param openChatUrl 수정할 오픈채팅 URL
	 * @param groupBeginDateTime 수정할 그룹 시작일시
	 * @param groupEndDateTime 수정할 그룹 종료일시
	 * @param maxParticipants 수정할 최대 참가자 수
	 */
	public void update(
		String groupName,
		GroupAddress groupAddress,
		String groupDescription,
		String openChatUrl,
		LocalDateTime groupBeginDateTime,
		LocalDateTime groupEndDateTime,
		Integer maxParticipants
	) {
		// 필수값 검증
		validateEmptyString(groupName, "그룹명은 필수값입니다.");
		validateNullData(groupBeginDateTime, "그룹 시작일시는 필수값입니다.");
		validateNullData(groupEndDateTime, "그룹 종료일시는 필수값입니다.");
		validateDateRange(groupBeginDateTime, groupEndDateTime, "그룹 시작일시는 종료일시보다 이전이어야 합니다.");
		validateNullData(groupAddress, "그룹 주소는 필수값입니다.");

		// maxParticipants 검증
		if (maxParticipants != null && maxParticipants <= 0) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_MAX_PARTICIPANTS_COUNT);
		}

		// 현재 참가자 수보다 작은 최대 참가자 수로 변경 불가
		if (maxParticipants != null && currentParticipants != null && maxParticipants < currentParticipants) {
			throw new ChallengeException(ChallengeExceptionMessage.MAX_PARTICIPANTS_LESS_THAN_CURRENT);
		}

		// 필드 업데이트
		this.groupName = groupName;
		this.groupAddress = groupAddress;
		this.groupDescription = groupDescription;
		this.openChatUrl = openChatUrl;
		this.groupBeginDateTime = groupBeginDateTime;
		this.groupEndDateTime = groupEndDateTime;
		this.maxParticipants = maxParticipants;
	}

	public void addParticipant() {
		if (isMaxParticipantsReached()) {
			throw new ChallengeException(ChallengeExceptionMessage.GROUP_IS_FULL);
		}
		if (currentParticipants == null) {
			currentParticipants = 0;
		}
		currentParticipants++;
	}

	public void removeParticipant() {
		if (currentParticipants != null && currentParticipants > 0) {
			currentParticipants--;
		}
	}

	public boolean isMaxParticipantsReached() {
		if (maxParticipants == null || currentParticipants == null) {
			return false;
		}
		return currentParticipants >= maxParticipants;
	}

	/**
	 * 현재 참가자 수를 반환합니다.
	 */
	public Integer getCurrentParticipants() {
		return currentParticipants;
	}

	public GroupStatus getGroupStatus() {
		if (isMaxParticipantsReached()) {
			return GroupStatus.COMPLETED;
		} else {
			return GroupStatus.RECRUITING;
		}
	}

	public boolean canParticipate(LocalDateTime now) {
		return !isMaxParticipantsReached() && now.isBefore(groupEndDateTime);
	}

	public boolean isActive(LocalDateTime now) {
		return now.isAfter(groupBeginDateTime) && now.isBefore(groupEndDateTime);
	}

	/**
	 * TeamChallenge와의 연관관계를 해제합니다.
	 */
	protected void disconnectFromTeamChallenge() {
		this.teamChallenge = null;
	}
}

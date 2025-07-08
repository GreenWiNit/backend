package com.example.green.domain.challenge.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.enums.GroupStatus;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 팀 챌린지 그룹 엔티티
 * 하나의 팀 챌린지 안에서 운영되는 세부 팀 그룹
 */
@Entity
@Table(indexes = {
	@Index(name = "idx_team_challenge_group_active", columnList = "groupStatus, groupBeginDateTime, groupEndDateTime"),
	@Index(name = "idx_team_challenge_group_by_challenge", columnList = "teamChallengeNo, groupStatus")
})
@Getter
@Setter(AccessLevel.PACKAGE) // 같은 패키지 내에서만 setter 사용 가능
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamChallengeGroup extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 100, nullable = false)
	private String groupName;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private GroupStatus groupStatus;

	@Column(length = 200)
	private String groupLocation;

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
	@JoinColumn(name = "team_challenge_id", insertable = false, updatable = false)
	private TeamChallenge teamChallenge;

	public static TeamChallengeGroup create(
		String groupName,
		GroupStatus groupStatus,
		LocalDateTime groupBeginDateTime,
		LocalDateTime groupEndDateTime,
		Integer maxParticipants,
		String groupLocation,
		String groupDescription,
		String openChatUrl
	) {
		// 필수 값 validate
		validateEmptyString(groupName, "그룹명은 필수값입니다.");
		validateNullData(groupStatus, "그룹 상태는 필수값입니다.");
		validateNullData(groupBeginDateTime, "그룹 시작일시는 필수값입니다.");
		validateNullData(groupEndDateTime, "그룹 종료일시는 필수값입니다.");
		validateDateRange(groupBeginDateTime, groupEndDateTime, "그룹 시작일시는 종료일시보다 이전이어야 합니다.");

		if (maxParticipants != null && maxParticipants <= 0) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_MAX_PARTICIPANTS_COUNT);
		}

		return new TeamChallengeGroup(
			groupName,
			groupStatus,
			groupBeginDateTime,
			groupEndDateTime,
			maxParticipants,
			groupLocation,
			groupDescription,
			openChatUrl
		);
	}

	private TeamChallengeGroup(
		String groupName,
		GroupStatus groupStatus,
		LocalDateTime groupBeginDateTime,
		LocalDateTime groupEndDateTime,
		Integer maxParticipants,
		String groupLocation,
		String groupDescription,
		String openChatUrl
	) {
		this.groupName = groupName;
		this.groupStatus = groupStatus;
		this.groupBeginDateTime = groupBeginDateTime;
		this.groupEndDateTime = groupEndDateTime;
		this.maxParticipants = maxParticipants;
		this.currentParticipants = 0;
		this.groupLocation = groupLocation;
		this.groupDescription = groupDescription;
		this.openChatUrl = openChatUrl;
	}

	/**
	 * 참가자 증가
	 */
	public void increaseParticipants() {
		if (currentParticipants == null) {
			currentParticipants = 0;
		}
		currentParticipants++;
	}

	/**
	 * 참가자 감소
	 */
	public void decreaseParticipants() {
		if (currentParticipants != null && currentParticipants > 0) {
			currentParticipants--;
		}
	}

	/**
	 * 최대 인원 확인
	 */
	public boolean isMaxParticipantsReached() {
		if (maxParticipants == null || currentParticipants == null) {
			return false;
		}
		return currentParticipants >= maxParticipants;
	}

	/**
	 * 참가 가능 여부 확인
	 */
	public boolean canParticipate(LocalDateTime now) {
		return !isMaxParticipantsReached()
			&& groupStatus == GroupStatus.RECRUITING
			&& now.isBefore(groupEndDateTime);
	}

	/**
	 * 그룹이 활성 상태인지 확인
	 */
	public boolean isActive(LocalDateTime now) {
		return groupStatus == GroupStatus.PROCEEDING
			&& now.isAfter(groupBeginDateTime)
			&& now.isBefore(groupEndDateTime);
	}
}

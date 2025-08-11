package com.example.green.domain.challenge.entity.group;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "challenge_groups",
	indexes = {
		@Index(name = "idx_challenge_groups_period", columnList = "begin_date_time, end_date_time"),
		@Index(name = "idx_challenge_groups_team_challenge_id", columnList = "team_challenge_id")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeGroup extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "challenge_group_id")
	private Long id;

	@Version
	private Long version;

	@Column(length = 30, nullable = false)
	private String teamCode;

	@Column(nullable = false)
	private Long teamChallengeId;

	@Column(nullable = false)
	private Long leaderId;

	private GroupBasicInfo basicInfo;
	private GroupAddress groupAddress;
	private GroupCapacity capacity;
	private GroupPeriod period;
	private GroupStatus status;

	@OneToMany(mappedBy = "challengeGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ChallengeGroupParticipation> participants = new LinkedHashSet<>();

	private ChallengeGroup(
		String teamCode, Long teamChallengeId, Long leaderId, GroupBasicInfo basicInfo,
		GroupAddress groupAddress, Integer maxParticipants, GroupPeriod period
	) {
		validateEmptyString(teamCode, "팀 코드는 필수값입니다.");
		validateAutoIncrementId(teamChallengeId, "팀 챌린지 식별자는 필수값입니다.");
		validateAutoIncrementId(leaderId, "리더 식별자는 필수값입니다.");
		this.teamCode = teamCode;
		this.teamChallengeId = teamChallengeId;
		this.leaderId = leaderId;
		this.basicInfo = basicInfo;
		this.groupAddress = groupAddress;
		this.capacity = GroupCapacity.of(maxParticipants);
		this.period = period;
		this.status = GroupStatus.RECRUITING;
	}

	public static ChallengeGroup create(
		String teamCode, Long teamChallengeId, Long leaderId, GroupBasicInfo basicInfo,
		GroupAddress groupAddress, Integer maxParticipants, GroupPeriod period
	) {
		ChallengeGroup challengeGroup = new ChallengeGroup(teamCode, teamChallengeId, leaderId, basicInfo,
			groupAddress, maxParticipants, period);

		ChallengeGroupParticipation participation = ChallengeGroupParticipation.fromLeader(challengeGroup, leaderId);
		challengeGroup.addParticipant(participation);

		return challengeGroup;
	}

	public void joinMember(Long memberId, LocalDateTime now) {
		if (capacity.isFull()) {
			throw new ChallengeException(ChallengeExceptionMessage.GROUP_IS_FULL);
		}
		if (!period.canParticipate(now)) {
			throw new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_PARTICIPATABLE);
		}
		ChallengeGroupParticipation participation = ChallengeGroupParticipation.fromMember(this, memberId);
		addParticipant(participation);
	}

	public void leaveMember(Long memberId) {
		if (isLeader(memberId)) {
			throw new ChallengeException(ChallengeExceptionMessage.LEADER_USE_BE_DELETE);
		}

		ChallengeGroupParticipation participation = findParticipationByMemberId(memberId);
		participants.remove(participation);
		capacity.decrease();
		this.status = determineStatus();
	}

	private void addParticipant(ChallengeGroupParticipation participation) {
		if (!participants.add(participation)) {
			throw new ChallengeException(ChallengeExceptionMessage.ALREADY_PARTICIPATING_IN_GROUP);
		}
		capacity.increase();
		this.status = determineStatus();
	}

	private ChallengeGroupParticipation findParticipationByMemberId(Long memberId) {
		return participants.stream()
			.filter(p -> p.matches(memberId))
			.findFirst()
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING));
	}

	public void updateBasicInfo(GroupBasicInfo groupBasicInfo) {
		this.basicInfo = groupBasicInfo;
	}

	public void updateAddress(GroupAddress groupAddress) {
		this.groupAddress = groupAddress;
	}

	public void updateCapacity(Integer maxParticipants) {
		this.capacity = GroupCapacity.update(capacity.getCurrentParticipants(), maxParticipants);
		this.status = determineStatus();
	}

	public void updatePeriod(GroupPeriod period) {
		this.period = period;
	}

	public boolean isLeader(Long memberId) {
		return memberId.equals(this.leaderId);
	}

	private GroupStatus determineStatus() {
		if (capacity.isFull()) {
			return GroupStatus.COMPLETED;
		}
		return GroupStatus.RECRUITING;
	}
}
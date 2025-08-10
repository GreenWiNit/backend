package com.example.green.domain.challenge.entity.group;

import static com.example.green.global.utils.EntityValidator.*;

import java.util.ArrayList;
import java.util.List;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "challenge_groups",
	indexes = {
		@Index(name = "idx_challenge_groups_period", columnList = "begin_date_time, end_date_time"),
		@Index(name = "idx_challenge_groups_team_challenge_id", columnList = "team_challenge_id")
	})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeGroup extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "challenge_group_id")
	private Long id;

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

	@OneToMany(mappedBy = "challengeGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChallengeGroupParticipation> participants = new ArrayList<>();

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

	private void addParticipant(ChallengeGroupParticipation participation) {
		if (capacity.isFull()) {
			throw new ChallengeException(ChallengeExceptionMessage.GROUP_IS_FULL);
		}

		participants.add(participation);
		capacity.increase();
	}
}

package com.example.green.domain.challenge.entity.group;

import static com.example.green.global.utils.EntityValidator.*;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_participations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"memberId"}, callSuper = false)
public class ChallengeGroupParticipation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long memberId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "challenge_group_id", nullable = false)
	private ChallengeGroup challengeGroup;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private GroupRole role;

	private Boolean certified;

	private ChallengeGroupParticipation(
		ChallengeGroup challengeGroup,
		Long memberId,
		GroupRole role
	) {
		validateNullData(challengeGroup, "그룹 정보는 필수입니다.");
		validateNullData(role, "역할은 필수입니다.");
		validateAutoIncrementId(memberId, "그룹원 식별자는 필수입니다.");
		this.challengeGroup = challengeGroup;
		this.memberId = memberId;
		this.role = role;
		this.certified = false;
	}

	public static ChallengeGroupParticipation fromLeader(ChallengeGroup challengeGroup, Long leaderId) {
		return new ChallengeGroupParticipation(challengeGroup, leaderId, GroupRole.LEADER);
	}

	public static ChallengeGroupParticipation fromMember(ChallengeGroup challengeGroup, Long memberId) {
		return new ChallengeGroupParticipation(challengeGroup, memberId, GroupRole.MEMBER);
	}

	public boolean matches(Long memberId) {
		return this.memberId.equals(memberId);
	}

	public boolean isLeader() {
		return this.role == GroupRole.LEADER;
	}

	public void confirmCertification() {
		this.certified = true;
	}
}
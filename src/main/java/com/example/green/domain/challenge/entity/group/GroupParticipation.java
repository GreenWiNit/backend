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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_participations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class GroupParticipation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long memberId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private GroupRoleType groupRoleType;

	private GroupParticipation(
		Group group,
		Long memberId,
		GroupRoleType role
	) {
		validateNullData(group, "그룹 정보는 필수입니다.");
		validateNullData(role, "역할은 필수입니다.");
		validateAutoIncrementId(memberId, "그룹원 식별자는 필수입니다.");
		this.group = group;
		this.memberId = memberId;
		this.groupRoleType = role;
	}

	public static GroupParticipation fromLeader(Group group, Long leaderId) {
		return new GroupParticipation(group, leaderId, GroupRoleType.LEADER);
	}

	public static GroupParticipation fromMember(Group group, Long memberId) {
		return new GroupParticipation(group, memberId, GroupRoleType.MEMBER);
	}
}

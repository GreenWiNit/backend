package com.example.green.domain.challenge.infra.querydsl.projections;

import static com.example.green.domain.challenge.entity.challenge.QParticipation.*;
import static com.example.green.domain.challenge.entity.group.QChallengeGroup.*;
import static com.example.green.domain.challenge.entity.group.QChallengeGroupParticipation.*;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminTeamParticipantDto;
import com.example.green.domain.challenge.controller.query.dto.group.AdminChallengeGroupDto;
import com.example.green.domain.challenge.controller.query.dto.group.ChallengeGroupDto;
import com.example.green.domain.challenge.controller.query.dto.group.MyChallengeGroupDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeGroupProjections {

	public static ConstructorExpression<MyChallengeGroupDto> toMyGroup(Long memberId) {
		return Projections.constructor(MyChallengeGroupDto.class,
			challengeGroup.id,
			challengeGroup.basicInfo.groupName,
			challengeGroup.groupAddress.sigungu,
			challengeGroup.period,
			challengeGroup.capacity,
			challengeGroup.leaderId.eq(memberId),
			challengeGroup.createdDate
		);
	}

	public static ConstructorExpression<AdminChallengeGroupDto> toGroupPageForAdmin() {
		return Projections.constructor(AdminChallengeGroupDto.class,
			challengeGroup.id,
			challengeGroup.teamCode,
			challengeGroup.basicInfo.groupName,
			challengeGroup.createdDate,
			challengeGroup.capacity.maxParticipants,
			challengeGroup.capacity.currentParticipants,
			challengeGroup.status
		);
	}

	public static ConstructorExpression<ChallengeGroupDto> toChallengeGroup() {
		return Projections.constructor(ChallengeGroupDto.class,
			challengeGroup.id,
			challengeGroup.basicInfo.groupName,
			challengeGroup.groupAddress.sigungu,
			challengeGroup.period,
			challengeGroup.capacity,
			challengeGroup.createdDate
		);
	}

	public static ConstructorExpression<AdminTeamParticipantDto> toParticipantByChallenge() {
		return Projections.constructor(
			AdminTeamParticipantDto.class,
			challengeGroup.teamCode,
			challengeGroupParticipation.memberId,
			participation.createdDate,
			challengeGroupParticipation.createdDate
		);
	}
}

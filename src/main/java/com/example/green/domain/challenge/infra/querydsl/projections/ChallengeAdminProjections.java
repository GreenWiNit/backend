package com.example.green.domain.challenge.infra.querydsl.projections;

import static com.example.green.domain.challenge.entity.challenge.QChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QParticipation.*;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalParticipationDto;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeAdminProjections {

	public static Expression<AdminChallengesDto> toChallengePage() {
		return Projections.constructor(
			AdminChallengesDto.class,
			challenge.id,
			challenge.code,
			challenge.info,
			challenge.display,
			challenge.createdDate
		);
	}

	public static Expression<AdminPersonalParticipationDto> toParticipation() {
		return Projections.constructor(
			AdminPersonalParticipationDto.class,
			participation.memberId,
			participation.createdDate,
			participation.certCount
		);
	}
}

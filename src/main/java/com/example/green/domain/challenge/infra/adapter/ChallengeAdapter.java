package com.example.green.domain.challenge.infra.adapter;

import static com.example.green.domain.challenge.entity.challenge.QParticipation.*;
import static com.example.green.domain.challenge.entity.challenge.QPersonalChallengeParticipation.*;
import static com.example.green.domain.challenge.entity.challenge.QTeamChallengeParticipation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.domain.challenge.service.ChallengeGroupService;
import com.example.green.infra.client.ChallengeClient;
import com.example.green.infra.client.dto.ChallengeDto;
import com.example.green.infra.client.dto.ChallengeGroupDto;
import com.example.green.infra.client.request.CertificationConfirmRequest;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeAdapter implements ChallengeClient {

	private final ChallengeGroupService challengeGroupService;
	private final PersonalChallengeQuery personalChallengeQuery;
	private final TeamChallengeQuery teamChallengeQuery;
	private final ChallengeGroupQuery challengeGroupQuery;
	private final JPAQueryFactory queryFactory;

	@Override
	public ChallengeDto getTeamChallenge(Long challengeId) {

		TeamChallenge challenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		return ChallengeDto.from(challenge);
	}

	@Override
	public ChallengeDto getPersonalChallengeByMemberAndDate(Long challengeId, Long memberId, LocalDate challengeDate) {
		PersonalChallenge challenge =
			personalChallengeQuery.getPersonalChallengeByMemberAndDate(challengeId, memberId, challengeDate);
		return ChallengeDto.from(challenge);
	}

	@Override
	public ChallengeGroupDto getChallengeGroup(Long groupId, Long memberId) {
		challengeGroupQuery.validateMembership(groupId, memberId);
		ChallengeGroup challengeGroup = challengeGroupQuery.getChallengeGroup(groupId);
		return ChallengeGroupDto.from(challengeGroup);
	}

	@Override
	public void confirmTeamCertification(CertificationConfirmRequest request) {
		challengeGroupService.confirmTeamCertification(request.groupId(), request.memberId());
	}

	@Override
	public Map<Long, Long> getCertificationCountByMembers(List<Long> memberIds) {
		if (memberIds == null || memberIds.isEmpty()) {
			return Map.of();
		}

		Map<Long, Long> resultMap = new HashMap<>();

		// PersonalChallengeParticipation
		List<Tuple> personalCounts = queryFactory
			.select(personalChallengeParticipation.memberId, personalChallengeParticipation.certCount.sum())
			.from(personalChallengeParticipation)
			.where(
				personalChallengeParticipation.memberId.in(memberIds),
				personalChallengeParticipation.deleted.eq(false)
			)
			.groupBy(personalChallengeParticipation.memberId)
			.fetch();

		for (Tuple tuple : personalCounts) {
			Long memberId = tuple.get(personalChallengeParticipation.memberId);
			Integer sum = tuple.get(personalChallengeParticipation.certCount.sum());
			resultMap.put(memberId, sum != null ? sum.longValue() : 0L);
		}

		// TeamChallengeParticipation
		List<Tuple> teamCounts = queryFactory
			.select(teamChallengeParticipation.memberId, teamChallengeParticipation.certCount.sum())
			.from(teamChallengeParticipation)
			.where(
				teamChallengeParticipation.memberId.in(memberIds),
				teamChallengeParticipation.deleted.eq(false)
			)
			.groupBy(teamChallengeParticipation.memberId)
			.fetch();

		for (Tuple tuple : teamCounts) {
			Long memberId = tuple.get(teamChallengeParticipation.memberId);
			Integer sum = tuple.get(teamChallengeParticipation.certCount.sum());
			resultMap.merge(memberId, sum != null ? sum.longValue() : 0L, Long::sum);
		}

		// Participation
		List<Tuple> participationCounts = queryFactory
			.select(participation.memberId, participation.certCount.sum())
			.from(participation)
			.where(
				participation.memberId.in(memberIds),
				participation.deleted.eq(false)
			)
			.groupBy(participation.memberId)
			.fetch();

		for (Tuple tuple : participationCounts) {
			Long memberId = tuple.get(participation.memberId);
			Integer sum = tuple.get(participation.certCount.sum());
			resultMap.merge(memberId, sum != null ? sum.longValue() : 0L, Long::sum);
		}

		return resultMap;
	}
}

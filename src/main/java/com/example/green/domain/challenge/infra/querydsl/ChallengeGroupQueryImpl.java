package com.example.green.domain.challenge.infra.querydsl;

import static com.example.green.domain.challenge.entity.challenge.QTeamChallengeParticipation.*;
import static com.example.green.domain.challenge.entity.group.QChallengeGroup.*;
import static com.example.green.domain.challenge.entity.group.QChallengeGroupParticipation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeGroupDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminTeamParticipantDto;
import com.example.green.domain.challenge.controller.query.dto.group.AdminChallengeGroupDto;
import com.example.green.domain.challenge.controller.query.dto.group.ChallengeGroupDetailDto;
import com.example.green.domain.challenge.controller.query.dto.group.ChallengeGroupDto;
import com.example.green.domain.challenge.controller.query.dto.group.MyChallengeGroupDto;
import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.ChallengeGroupRepository;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChallengeGroupQueryImpl implements ChallengeGroupQuery {

	private final ChallengeGroupRepository challengeGroupRepository;
	private final JPAQueryFactory queryFactory;

	@Override
	public ChallengeGroup getChallengeGroup(Long groupId) {
		return challengeGroupRepository.findById(groupId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.CHALLENGE_GROUP_NOT_FOUND));
	}

	@Override
	public void validateLeader(Long groupId, Long leaderId) {
		if (!challengeGroupRepository.existsByIdAndLeaderId(groupId, leaderId)) {
			throw new ChallengeException(ChallengeExceptionMessage.NOT_GROUP_LEADER);
		}
	}

	public CursorTemplate<String, MyChallengeGroupDto> findMyGroup(
		Long challengeId, String cursor, Integer size, Long memberId
	) {
		List<Long> participatingGroupIds = queryFactory
			.select(challengeGroupParticipation.challengeGroup.id)
			.from(challengeGroupParticipation)
			.where(challengeGroupParticipation.memberId.eq(memberId))
			.fetch();

		if (participatingGroupIds.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		List<MyChallengeGroupDto> groups = queryFactory
			.select(Projections.constructor(MyChallengeGroupDto.class,
				challengeGroup.id,
				challengeGroup.basicInfo.groupName,
				challengeGroup.groupAddress.sigungu,
				challengeGroup.period,
				challengeGroup.capacity,
				challengeGroup.leaderId.eq(memberId),
				challengeGroup.createdDate
			))
			.from(challengeGroup)
			.where(
				challengeGroup.teamChallengeId.eq(challengeId),
				challengeGroup.id.in(participatingGroupIds),
				fromCondition(cursor)
			)
			.orderBy(
				challengeGroup.createdDate.desc(),
				challengeGroup.id.desc()
			)
			.limit(size + 1)
			.fetch();

		return CursorTemplate.from(groups, size, dto -> dto.createdDate() + "," + dto.id());
	}

	@Override
	public ChallengeGroupDetailDto getGroupDetail(Long groupId, Long memberId) {
		boolean participating = challengeGroupRepository.existMembership(groupId, memberId);
		ChallengeGroup challengeGroup = getChallengeGroup(groupId);
		return ChallengeGroupDetailDto.from(challengeGroup, participating, memberId);
	}

	@Override
	public CursorTemplate<String, ChallengeGroupDto> findAllGroupByCursor(Long challengeId, String cursor, Integer size,
		Long memberId) {
		List<ChallengeGroupDto> groups = queryFactory
			.select(Projections.constructor(ChallengeGroupDto.class,
				challengeGroup.id,
				challengeGroup.basicInfo.groupName,
				challengeGroup.groupAddress.sigungu,
				challengeGroup.period,
				challengeGroup.capacity,
				challengeGroup.createdDate
			))
			.from(challengeGroup)
			.where(
				challengeGroup.teamChallengeId.eq(challengeId),
				challengeGroup.leaderId.ne(memberId),
				fromCondition(cursor)
			)
			.orderBy(
				challengeGroup.createdDate.desc(),
				challengeGroup.id.desc()
			)
			.limit(size + 1)
			.fetch();

		return CursorTemplate.from(groups, size, dto -> dto.createdDate() + "," + dto.id());
	}

	@Override
	public PageTemplate<AdminChallengeGroupDto> findGroupPaging(Integer page, Integer size) {
		long count = challengeGroupRepository.count();
		Pagination pagination = Pagination.of(count, page, size);

		List<AdminChallengeGroupDto> result = queryFactory
			.select(Projections.constructor(AdminChallengeGroupDto.class,
				challengeGroup.id,
				challengeGroup.teamCode,
				challengeGroup.basicInfo.groupName,
				challengeGroup.createdDate,
				challengeGroup.capacity.maxParticipants,
				challengeGroup.capacity.currentParticipants,
				challengeGroup.status
			))
			.from(challengeGroup)
			.orderBy(challengeGroup.createdDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();

		return PageTemplate.of(result, pagination);
	}

	public AdminChallengeGroupDetailDto getGroupDetailForAdmin(Long groupId) {
		ChallengeGroup challengeGroup = getChallengeGroup(groupId);
		return AdminChallengeGroupDetailDto.from(challengeGroup);
	}

	public PageTemplate<AdminTeamParticipantDto> findParticipantByChallenge(
		Long challengeId, Integer page, Integer size
	) {
		long count = executeParticipationDetailCountQuery(challengeId);
		Pagination pagination = Pagination.of(count, page, size);

		List<AdminTeamParticipantDto> result = queryFactory
			.select(Projections.constructor(
				AdminTeamParticipantDto.class,
				challengeGroup.teamCode,
				challengeGroupParticipation.memberId,
				teamChallengeParticipation.participatedAt,
				challengeGroupParticipation.createdDate
			))
			.from(challengeGroup)
			.join(challengeGroup.participants, challengeGroupParticipation)
			.join(teamChallengeParticipation).on(teamChallengeParticipation.teamChallenge.id.eq(challengeId)
				.and(teamChallengeParticipation.memberId.eq(challengeGroupParticipation.memberId)))
			.where(challengeGroup.teamChallengeId.eq(challengeId))
			.orderBy(challengeGroupParticipation.createdDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
		return PageTemplate.of(result, pagination);
	}

	@Override
	public List<AdminTeamParticipantDto> findParticipantByChallengeForExcel(Long challengeId) {
		return queryFactory
			.select(Projections.constructor(
				AdminTeamParticipantDto.class,
				challengeGroup.teamCode,
				challengeGroupParticipation.memberId,
				teamChallengeParticipation.participatedAt,
				challengeGroupParticipation.createdDate
			))
			.from(challengeGroup)
			.join(challengeGroup.participants, challengeGroupParticipation)
			.join(teamChallengeParticipation).on(teamChallengeParticipation.teamChallenge.id.eq(challengeId)
				.and(teamChallengeParticipation.memberId.eq(challengeGroupParticipation.memberId)))
			.where(challengeGroup.teamChallengeId.eq(challengeId))
			.orderBy(challengeGroupParticipation.createdDate.desc())
			.fetch();
	}

	public ChallengeGroup getChallengeGroup(Long groupId, Long memberId) {
		if (!challengeGroupRepository.existMembership(groupId, memberId)) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_GROUP_MEMBERSHIP);
		}
		return getChallengeGroup(groupId);
	}

	public long executeParticipationDetailCountQuery(Long challengeId) {
		return Optional.ofNullable(queryFactory
				.select(challengeGroupParticipation.count())
				.from(challengeGroup)
				.join(challengeGroup.participants, challengeGroupParticipation)
				.join(teamChallengeParticipation)
				.on(teamChallengeParticipation.teamChallenge.id.eq(challengeId)
					.and(teamChallengeParticipation.memberId.eq(challengeGroupParticipation.memberId)))
				.where(challengeGroup.teamChallengeId.eq(challengeId))
				.fetchOne())
			.orElseThrow(() -> new IllegalStateException("팀 챌린지 참가자 카운트 실패"));
	}

	public BooleanExpression fromCondition(String cursor) {
		if (cursor == null) {
			return null;
		}

		String[] parts = cursor.split(",");
		LocalDateTime dateCursor = LocalDateTime.parse(parts[0]);
		Long idCursor = Long.parseLong(parts[1]);
		return challengeGroup.createdDate.lt(dateCursor)
			.or(challengeGroup.createdDate.eq(dateCursor)
				.and(challengeGroup.id.lt(idCursor)));
	}
}

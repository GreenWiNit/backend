package com.example.green.domain.challenge.infra.querydsl;

import static com.example.green.domain.challenge.entity.group.QChallengeGroup.*;
import static com.example.green.domain.challenge.entity.group.QChallengeGroupParticipation.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.controller.dto.ChallengeGroupDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeGroupDto;
import com.example.green.domain.challenge.controller.dto.MyChallengeGroupDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeGroupDto;
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

@Repository
@RequiredArgsConstructor
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
			.join(challengeGroup.participants, challengeGroupParticipation)
			.where(
				challengeGroup.teamChallengeId.eq(challengeId),
				challengeGroupParticipation.memberId.eq(memberId),
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
		return ChallengeGroupDetailDto.from(challengeGroup, participating);
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
			.join(challengeGroup.participants, challengeGroupParticipation)
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

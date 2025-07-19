package com.example.green.domain.challenge.repository;

import static com.example.green.domain.challenge.entity.QTeamChallengeGroup.*;
import static com.example.green.domain.challengecert.entity.QTeamChallengeGroupParticipation.*;
import static com.example.green.domain.challengecert.entity.QTeamChallengeParticipation.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challengecert.entity.enums.GroupRoleType;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TeamChallengeGroupRepositoryImpl implements TeamChallengeGroupRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorTemplate<Long, TeamChallengeGroupListResponseDto> findGroupsByChallengeIdAndCursor(
		Long challengeId,
		Long cursor,
		int size,
		Long memberId
	) {
		List<TeamChallengeGroup> groups = queryFactory
			.selectFrom(teamChallengeGroup)
			.where(
				teamChallengeGroup.teamChallenge.id.eq(challengeId),
				cursorCondition(cursor)
			)
			.orderBy(teamChallengeGroup.id.desc())
			.limit(size + 1)
			.fetch();

		if (groups.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		boolean hasNext = groups.size() > size;
		if (hasNext) {
			groups = groups.subList(0, size);
		}

		List<TeamChallengeGroupListResponseDto> dtos = groups.stream()
			.map(group -> toGroupListDto(group, memberId))
			.toList();

		if (hasNext) {
			Long nextCursor = groups.getLast().getId();
			return CursorTemplate.ofWithNextCursor(nextCursor, dtos);
		} else {
			return CursorTemplate.of(dtos);
		}
	}

	private BooleanExpression cursorCondition(Long cursor) {
		return cursor != null ? teamChallengeGroup.id.lt(cursor) : null;
	}

	private TeamChallengeGroupListResponseDto toGroupListDto(TeamChallengeGroup group, Long memberId) {
		// 해당 사용자가 이 그룹의 리더인지 확인
		Boolean isLeader = isUserLeaderOfGroup(group.getId(), memberId);

		return new TeamChallengeGroupListResponseDto(
			group.getId(),
			group.getGroupName(),
			group.getGroupAddress() != null ? group.getGroupAddress().getFullAddress() : null,
			group.getGroupBeginDateTime(),
			group.getGroupEndDateTime(),
			group.getCurrentParticipants(),
			group.getMaxParticipants(),
			group.getGroupStatus(),
			isLeader
		);
	}

	private Boolean isUserLeaderOfGroup(Long groupId, Long memberId) {
		if (memberId == null) {
			return false;
		}

		Long count = queryFactory
			.select(teamChallengeGroupParticipation.count())
			.from(teamChallengeGroupParticipation)
			.join(teamChallengeGroupParticipation.teamChallengeParticipation, teamChallengeParticipation)
			.where(
				teamChallengeGroupParticipation.teamChallengeGroup.id.eq(groupId),
				teamChallengeParticipation.member.id.eq(memberId),
				teamChallengeGroupParticipation.groupRoleType.eq(GroupRoleType.LEADER)
			)
			.fetchOne();

		return count != null && count > 0;
	}
}

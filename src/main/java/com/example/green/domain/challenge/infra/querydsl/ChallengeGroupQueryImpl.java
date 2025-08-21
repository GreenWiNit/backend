package com.example.green.domain.challenge.infra.querydsl;

import static com.example.green.domain.challenge.infra.querydsl.predicates.ChallengeGroupPredicates.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.example.green.domain.challenge.infra.querydsl.executor.ChallengeGroupQueryExecutor;
import com.example.green.domain.challenge.infra.querydsl.predicates.ChallengeGroupPredicates;
import com.example.green.domain.challenge.repository.ChallengeGroupRepository;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChallengeGroupQueryImpl implements ChallengeGroupQuery {

	private final ChallengeGroupRepository challengeGroupRepository;
	private final ChallengeGroupQueryExecutor executor;

	@Override
	public ChallengeGroup getChallengeGroup(Long groupId) {
		return challengeGroupRepository.findById(groupId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.CHALLENGE_GROUP_NOT_FOUND));
	}

	public CursorTemplate<String, MyChallengeGroupDto> findMyGroup(
		Long challengeId, String cursor, Integer size, Long memberId
	) {
		Map<Long, Boolean> certifiedMap = executor.executeMyGroupCertifiedMap(memberId);
		if (certifiedMap.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		List<Long> participatingGroupIds = new ArrayList<>(certifiedMap.keySet());
		BooleanExpression condition = getMyGroupCondition(challengeId, participatingGroupIds, cursor);
		List<MyChallengeGroupDto> groups = executor.executeMyGroupQuery(size, memberId, condition);
		groups.forEach(group -> group.setCertified(certifiedMap.get(group.getId())));

		return CursorTemplate.from(groups, size, MyChallengeGroupDto::getCursor);
	}

	public ChallengeGroupDetailDto getGroupDetail(Long groupId, Long memberId) {
		boolean participating = challengeGroupRepository.existMembership(groupId, memberId);
		ChallengeGroup challengeGroup = getChallengeGroup(groupId);
		return ChallengeGroupDetailDto.from(challengeGroup, participating, memberId);
	}

	public CursorTemplate<String, ChallengeGroupDto> findAllGroupByCursor(
		Long challengeId, String cursor, Integer size, Long memberId
	) {
		BooleanExpression condition = ChallengeGroupPredicates.getGroupCursorCondition(challengeId, cursor, memberId);
		List<ChallengeGroupDto> groups = executor.executeGroupCursorQuery(size, condition);
		return CursorTemplate.from(groups, size, dto -> dto.createdDate() + "," + dto.id());
	}

	@Override
	public PageTemplate<AdminChallengeGroupDto> findGroupPaging(Integer page, Integer size) {
		long count = challengeGroupRepository.count();
		Pagination pagination = Pagination.of(count, page, size);
		List<AdminChallengeGroupDto> result = executor.executeGroupPageForAdmin(pagination);
		return PageTemplate.of(result, pagination);
	}

	public AdminChallengeGroupDetailDto getGroupDetailForAdmin(Long groupId) {
		ChallengeGroup challengeGroup = getChallengeGroup(groupId);
		return AdminChallengeGroupDetailDto.from(challengeGroup);
	}

	public PageTemplate<AdminTeamParticipantDto> findParticipantByChallenge(
		Long challengeId, Integer page, Integer size
	) {
		long count = executor.executeParticipantCountQuery(challengeId);
		Pagination pagination = Pagination.of(count, page, size);
		List<AdminTeamParticipantDto> result = executor.executeGroupParticipantQuery(challengeId, pagination);
		return PageTemplate.of(result, pagination);
	}

	public List<AdminTeamParticipantDto> findParticipantByChallengeForExcel(Long challengeId) {
		return executor.executeGroupParticipantForExcelQuery(challengeId);
	}

	public ChallengeGroup getChallengeGroup(Long groupId, Long memberId) {
		if (!challengeGroupRepository.existMembership(groupId, memberId)) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_GROUP_MEMBERSHIP);
		}
		return getChallengeGroup(groupId);
	}

	public void validateActivityDateParticipation(Long memberId, Long challengeId, LocalDate activityDate) {
		LocalDateTime startOfDay = activityDate.atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1);

		if (challengeGroupRepository.existsParticipationOnActivityDate(memberId, challengeId, startOfDay, endOfDay)) {
			throw new ChallengeException(ChallengeExceptionMessage.ALREADY_PARTICIPATED_ON_THIS_DATE);
		}
	}

	@Override
	public ChallengeGroup getChallengeGroupByTeamCode(String teamCode) {
		return challengeGroupRepository.findByTeamCode(teamCode)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.CHALLENGE_GROUP_NOT_FOUND));
	}
}

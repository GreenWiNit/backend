package com.example.green.domain.challenge.infra.querydsl;

import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;
import static com.example.green.domain.challenge.infra.querydsl.predicates.PersonalChallengePredicates.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalParticipationDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeStatus;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.infra.querydsl.executor.PersonalChallengeQueryExecutor;
import com.example.green.domain.challenge.infra.querydsl.predicates.PersonalChallengePredicates;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
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
public class PersonalChallengeQueryImpl implements PersonalChallengeQuery {

	private final PersonalChallengeRepository personalChallengeRepository;
	private final PersonalChallengeQueryExecutor executor;

	public CursorTemplate<Long, ChallengeDto> findMyParticipationByCursor(Long memberId, Long cursor, int size) {
		BooleanExpression condition = myParticipationCondition(memberId, cursor);
		List<ChallengeDto> participationChallenges = executor.executeParticipationQueryForClient(condition, size);
		return CursorTemplate.from(participationChallenges, size, ChallengeDto::id);
	}

	public CursorTemplate<Long, ChallengeDto> findPersonalChallengesByCursor(
		Long cursor,
		int size,
		ChallengeStatus status,
		LocalDateTime now
	) {
		BooleanExpression condition = PersonalChallengePredicates.activeChallengeCondition(cursor, status, now);
		List<ChallengeDto> challenges = executor.executeChallengesQueryForClient(condition, size);
		return CursorTemplate.from(challenges, size, ChallengeDto::id);
	}

	public PersonalChallenge getPersonalChallengeById(Long challengeId) {
		return personalChallengeRepository.findById(challengeId)
			.orElseThrow(() -> new ChallengeException(CHALLENGE_NOT_FOUND));
	}

	public ChallengeDetailDto findPersonalChallenge(Long challengeId, Long memberId) {
		BooleanExpression participationExists = memberParticipationExists(challengeId, memberId);
		return executor.executeChallengeDetailQuery(participationExists, challengeId);
	}

	public AdminChallengeDetailDto getChallengeDetail(Long challengeId) {
		PersonalChallenge personalChallenge = getPersonalChallengeById(challengeId);
		return AdminChallengeDetailDto.from(personalChallenge);
	}

	@Override
	public PageTemplate<AdminPersonalChallengesDto> findChallengePage(Integer page, Integer size) {
		long count = personalChallengeRepository.count();
		Pagination pagination = Pagination.of(count, page, size);
		List<AdminPersonalChallengesDto> result = executor.executeChallengesQueryForAdmin(pagination);
		return PageTemplate.of(result, pagination);
	}

	@Override
	public List<AdminPersonalChallengesDto> findChallengePageForExcel() {
		return executor.executeChallengeExcelQueryForAdmin();
	}

	@Override
	public PageTemplate<AdminPersonalParticipationDto> findParticipantByChallenge(
		Long challengeId, Integer page, Integer size
	) {
		long count = personalChallengeRepository.countParticipantByChallenge(challengeId);
		Pagination pagination = Pagination.of(count, page, size);
		List<AdminPersonalParticipationDto> result = executor.executeParticipantQueryForAdmin(pagination, challengeId);
		return PageTemplate.of(result, pagination);
	}

	@Override
	public List<AdminPersonalParticipationDto> findParticipantByChallengeForExcel(Long challengeId) {
		return executor.executeParticipantQueryForExcel(challengeId);
	}

	@Override
	public PersonalChallenge getPersonalChallengeByMemberAndDate(
		Long challengeId, Long memberId, LocalDate challengeDate
	) {
		if (!personalChallengeRepository.existsMembership(challengeId, memberId)) {
			throw new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING);
		}
		PersonalChallenge challenge = getPersonalChallengeById(challengeId);
		if (!challenge.isActive(challengeDate)) {
			throw new ChallengeException(INACTIVE_CHALLENGE);
		}
		return challenge;
	}
}

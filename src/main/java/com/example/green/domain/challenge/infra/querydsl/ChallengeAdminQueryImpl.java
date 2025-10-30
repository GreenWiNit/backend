package com.example.green.domain.challenge.infra.querydsl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalParticipationDto;
import com.example.green.domain.challenge.entity.challenge.Challenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.infra.querydsl.executor.ChallengeAdminQueryExecutor;
import com.example.green.domain.challenge.repository.ChallengeRepository;
import com.example.green.domain.challenge.repository.query.ChallengeAdminQuery;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChallengeAdminQueryImpl implements ChallengeAdminQuery {

	private final ChallengeRepository challengeRepository;
	private final ChallengeAdminQueryExecutor executor;

	@Override
	public AdminChallengeDetailDto getChallengeDetail(Long id) {
		Challenge challenge = challengeRepository.findByIdWithThrow(id);
		return AdminChallengeDetailDto.from(challenge);
	}

	@Override
	public PageTemplate<AdminChallengesDto> findChallengePage(Integer page, Integer size, ChallengeType type) {
		long count = challengeRepository.countChallengeByType(type);
		Pagination pagination = Pagination.of(count, page, size);
		List<AdminChallengesDto> result = executor.executeChallengePageQuery(pagination);
		return PageTemplate.of(result, pagination);
	}

	@Override
	public List<AdminChallengesDto> findChallengePageExcel() {
		return executor.executeChallengePageExcelQuery();
	}

	@Override
	public PageTemplate<AdminPersonalParticipationDto> findParticipantByChallenge(Long id, Integer page, Integer size) {
		long count = challengeRepository.countParticipantByChallenge(id);
		Pagination pagination = Pagination.of(count, page, size);
		List<AdminPersonalParticipationDto> result = executor.executeParticipantQuery(pagination, id);
		return PageTemplate.of(result, pagination);
	}

	@Override
	public List<AdminPersonalParticipationDto> findParticipantExcelByChallenge(Long id) {
		return executor.executeParticipantQueryForExcel(id);
	}
}

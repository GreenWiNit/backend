package com.example.green.domain.challenge.repository.query;

import java.time.LocalDateTime;
import java.util.List;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.PersonalParticipationDto;
import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeStatus;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface PersonalChallengeQuery {

	CursorTemplate<Long, ChallengeDto> findMyParticipationByCursor(Long memberId, Long cursor, int size);

	CursorTemplate<Long, ChallengeDto> findPersonalChallengesByCursor(
		Long cursor,
		int size,
		ChallengeStatus status,
		LocalDateTime now
	);

	PersonalChallenge getPersonalChallengeById(Long challengeId);

	ChallengeDetailDto findPersonalChallenge(Long challengeId, Long memberId);

	AdminChallengeDetailDto getChallengeDetail(Long challengeId);

	PageTemplate<AdminPersonalChallengesDto> findChallengePage(Integer page, Integer size);

	List<AdminPersonalChallengesDto> findChallengePageForExcel();

	PageTemplate<PersonalParticipationDto> findParticipantByChallenge(Long challengeId, Integer page, Integer size);

	List<PersonalParticipationDto> findParticipantByChallengeForExcel(Long challengeId);
}

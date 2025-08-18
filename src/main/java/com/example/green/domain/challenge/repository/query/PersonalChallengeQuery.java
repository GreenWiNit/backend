package com.example.green.domain.challenge.repository.query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalParticipationDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDtoV2;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface PersonalChallengeQuery {

	CursorTemplate<Long, ChallengeDto> findMyParticipationByCursor(Long memberId, Long cursor, int size);

	CursorTemplate<Long, ChallengeDto> findPersonalChallengesByCursor(
		Long cursor,
		int size,
		LocalDateTime now
	);

	PersonalChallenge getPersonalChallengeById(Long challengeId);

	ChallengeDetailDto findPersonalChallenge(Long challengeId, Long memberId);

	AdminChallengeDetailDto getChallengeDetail(Long challengeId);

	PageTemplate<AdminPersonalChallengesDto> findChallengePage(Integer page, Integer size);

	List<AdminPersonalChallengesDto> findChallengePageForExcel();

	PageTemplate<AdminPersonalParticipationDto> findParticipantByChallenge(
		Long challengeId, Integer page, Integer size);

	List<AdminPersonalParticipationDto> findParticipantByChallengeForExcel(Long challengeId);

	PersonalChallenge getPersonalChallengeByMemberAndDate(Long challengeId, Long memberId, LocalDate challengeDate);

	ChallengeDetailDtoV2 findPersonalChallengeV2(Long challengeId, Long memberId);
}

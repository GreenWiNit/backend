package com.example.green.domain.challenge.repository.query;

import java.time.LocalDateTime;
import java.util.List;

import com.example.green.domain.challenge.controller.dto.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeStatus;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface PersonalChallengeQuery {

	CursorTemplate<Long, ChallengeListResponseDto> findMyParticipationByCursor(Long memberId, Long cursor, int size);

	CursorTemplate<Long, ChallengeListResponseDto> findPersonalChallengesByCursor(
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
}

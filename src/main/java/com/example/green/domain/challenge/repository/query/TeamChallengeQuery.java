package com.example.green.domain.challenge.repository.query;

import java.time.LocalDateTime;
import java.util.List;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminTeamChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeStatus;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface TeamChallengeQuery {

	CursorTemplate<Long, ChallengeDto> findMyParticipationByCursor(Long memberId, Long cursor, int size);

	CursorTemplate<Long, ChallengeDto> findTeamChallengesByCursor(
		Long cursor, int size, ChallengeStatus challengeStatus, LocalDateTime now);

	ChallengeDetailDto findTeamChallenge(Long challengeId, Long memberId);

	TeamChallenge getTeamChallengeById(Long challengeId);

	PageTemplate<AdminTeamChallengesDto> findChallengePage(Integer page, Integer size);

	AdminChallengeDetailDto getChallengeDetail(Long challengeId);

	List<AdminTeamChallengesDto> findTeamChallengeForExcel();

	void validateGroupPeriod(Long challengeId, LocalDateTime beginDateTime, LocalDateTime endDateTime);
}

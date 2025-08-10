package com.example.green.domain.challenge.repository.query;

import java.time.LocalDateTime;
import java.util.List;

import com.example.green.domain.challenge.controller.dto.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengesDto;
import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeStatus;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface TeamChallengeQuery {

	CursorTemplate<Long, ChallengeListResponseDto> findMyParticipationByCursor(Long memberId, Long cursor, int size);

	CursorTemplate<Long, ChallengeListResponseDto> findTeamChallengesByCursor(
		Long cursor, int size, ChallengeStatus challengeStatus, LocalDateTime now);

	ChallengeDetailDto findTeamChallenge(Long challengeId, Long memberId);

	TeamChallenge getTeamChallengeById(Long challengeId);

	PageTemplate<AdminTeamChallengesDto> findChallengePage(Integer page, Integer size);

	AdminChallengeDetailDto getChallengeDetail(Long challengeId);

	List<AdminTeamChallengesDto> findTeamChallengeForExcel();
}

package com.example.green.domain.challenge.repository.query;

import java.util.List;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalParticipationDto;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.global.api.page.PageTemplate;

public interface ChallengeAdminQuery {

	AdminChallengeDetailDto getChallengeDetail(Long id);

	// 팀 & 개인 챌린지 페이지 조회
	PageTemplate<AdminChallengesDto> findChallengePage(Integer page, Integer size, ChallengeType type);

	List<AdminChallengesDto> findChallengePageExcel();

	PageTemplate<AdminPersonalParticipationDto> findParticipantByChallenge(Long id, Integer page, Integer size);

	List<AdminPersonalParticipationDto> findParticipantExcelByChallenge(Long id);
}

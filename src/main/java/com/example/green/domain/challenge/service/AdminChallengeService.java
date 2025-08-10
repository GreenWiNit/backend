package com.example.green.domain.challenge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeParticipantListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.entity.group.TeamChallengeGroup;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.PersonalChallengeParticipationRepository;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeGroupRepository;
import com.example.green.domain.challenge.entity.group.TeamChallengeGroupParticipation;
import com.example.green.domain.challengecert.repository.TeamChallengeGroupParticipationRepository;
import com.example.green.domain.challengecert.repository.dao.ChallengeParticipantDao;
import com.example.green.global.api.page.CursorTemplate;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminChallengeService {
	private static final int DEFAULT_PAGE_SIZE = 10;

	private final PersonalChallengeRepository personalChallengeRepository;
	private final TeamChallengeGroupRepository teamChallengeGroupRepository;
	private final PersonalChallengeParticipationRepository personalChallengeParticipationRepository;
	private final TeamChallengeGroupParticipationRepository teamChallengeGroupParticipationRepository;

	/**
	 * 챌린지 참여자 목록을 조회합니다.
	 */
	public CursorTemplate<Long, AdminChallengeParticipantListResponseDto> getChallengeParticipants(Long challengeId,
		Long cursor) {
		// 챌린지 존재 여부 확인
		personalChallengeRepository.findById(challengeId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.ADMIN_CHALLENGE_NOT_FOUND));

		// Repository에서 DAO 조회
		CursorTemplate<Long, ChallengeParticipantDao> daoResult =
			personalChallengeParticipationRepository.findParticipantsByChallengeIdCursor(challengeId, cursor,
				DEFAULT_PAGE_SIZE);

		// DAO를 DTO로 변환 (DTO의 static factory 메서드 사용)
		List<AdminChallengeParticipantListResponseDto> dtos = daoResult.content().stream()
			.map(AdminChallengeParticipantListResponseDto::from)
			.toList();

		// CursorTemplate 재구성
		if (daoResult.nextCursor() != null) {
			return CursorTemplate.ofWithNextCursor(daoResult.nextCursor(), dtos);
		} else {
			return CursorTemplate.of(dtos);
		}
	}

	/**
	 * 팀 챌린지 그룹 목록을 조회합니다.
	 */
	public CursorTemplate<Long, AdminTeamChallengeGroupListResponseDto> getGroups(Long cursor) {
		return teamChallengeGroupRepository.findAllForAdminByCursor(cursor, DEFAULT_PAGE_SIZE);
	}

	/**
	 * 팀 챌린지 그룹 상세 정보를 조회합니다.
	 */
	public AdminTeamChallengeGroupDetailResponseDto getGroupDetail(Long groupId) {
		TeamChallengeGroup group = teamChallengeGroupRepository.findById(groupId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.ADMIN_TEAM_CHALLENGE_GROUP_NOT_FOUND));

		List<TeamChallengeGroupParticipation> participants
			= teamChallengeGroupParticipationRepository.findByTeamChallengeGroup(group);

		return AdminTeamChallengeGroupDetailResponseDto.from(group, participants);
	}
}

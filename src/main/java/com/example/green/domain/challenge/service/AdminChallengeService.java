package com.example.green.domain.challenge.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDisplayStatusUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeImageUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeParticipantListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminPersonalChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeListResponseDto;
import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.PersonalChallengeParticipationRepository;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeGroupRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.domain.challengecert.entity.TeamChallengeGroupParticipation;
import com.example.green.domain.challengecert.repository.TeamChallengeGroupParticipationRepository;
import com.example.green.domain.challengecert.repository.dao.ChallengeParticipantDao;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminChallengeService {
	private static final int DEFAULT_PAGE_SIZE = 10;

	private final PersonalChallengeRepository personalChallengeRepository;
	private final TeamChallengeRepository teamChallengeRepository;
	private final TeamChallengeGroupRepository teamChallengeGroupRepository;
	private final PersonalChallengeParticipationRepository personalChallengeParticipationRepository;
	private final TeamChallengeGroupParticipationRepository teamChallengeGroupParticipationRepository;
	private final TimeUtils timeUtils;

	public Long createPersonalChallenge(AdminChallengeCreateRequestDto request) {
		long count = personalChallengeRepository.countChallengesByCreatedDate(timeUtils.now());
		String challengeCode = CodeGenerator.generatePersonalCode(timeUtils.now(), count + 1);
		PersonalChallenge challenge = PersonalChallenge.create(
			challengeCode, request.challengeName(), request.challengeImageUrl(), request.challengeContent(),
			request.challengePoint(), request.beginDateTime(), request.endDateTime()
		);

		PersonalChallenge savedChallenge = personalChallengeRepository.save(challenge);
		return savedChallenge.getId();
	}

	public Long createTeamChallenge(AdminChallengeCreateRequestDto request) {
		long count = teamChallengeRepository.countChallengesByCreatedDate(timeUtils.now());
		String challengeCode = CodeGenerator.generateTeamCode(timeUtils.now(), count + 1);
		TeamChallenge challenge = TeamChallenge.create(
			challengeCode, request.challengeName(), request.challengeImageUrl(), request.challengeContent(),
			request.challengePoint(), request.beginDateTime(), request.endDateTime()
		);

		TeamChallenge saved = teamChallengeRepository.save(challenge);
		return saved.getId();
	}

	public void updateChallenge(Long challengeId, AdminChallengeUpdateRequestDto request) {
		try {
			var personalChallenge = personalChallengeRepository.findById(challengeId);
			if (personalChallenge.isPresent()) {
				personalChallenge.get().updateBasicInfo(
					request.challengeName(),
					request.challengePoint(),
					request.beginDateTime(),
					request.endDateTime(),
					request.challengeContent()
				);
				return;
			}

			var teamChallenge = teamChallengeRepository.findById(challengeId);
			if (teamChallenge.isPresent()) {
				teamChallenge.get().updateBasicInfo(
					request.challengeName(),
					request.challengePoint(),
					request.beginDateTime(),
					request.endDateTime(),
					request.challengeContent()
				);
				return;
			}

			// 둘 다 없으면 예외 발생
			throw new ChallengeException(ChallengeExceptionMessage.ADMIN_CHALLENGE_NOT_FOUND);
		} catch (ChallengeException e) {
			throw e;
		} catch (Exception e) {
			throw new ChallengeException(ChallengeExceptionMessage.ADMIN_CHALLENGE_UPDATE_FAILED);
		}
	}

	/**
	 * 챌린지 이미지를 업데이트합니다.
	 */
	public AdminChallengeDetailResponseDto updateChallengeImage(Long challengeId,
		AdminChallengeImageUpdateRequestDto request) {
		try {
			// PersonalChallenge인지 확인
			var personalChallenge = personalChallengeRepository.findById(challengeId);
			if (personalChallenge.isPresent()) {
				personalChallenge.get().updateImage(request.challengeImageUrl());
				return AdminChallengeDetailResponseDto.from(personalChallenge.get());
			}

			// TeamChallenge인지 확인
			var teamChallenge = teamChallengeRepository.findById(challengeId);
			if (teamChallenge.isPresent()) {
				teamChallenge.get().updateImage(request.challengeImageUrl());
				return AdminChallengeDetailResponseDto.from(teamChallenge.get());
			}

			// 둘 다 없으면 예외 발생
			throw new ChallengeException(ChallengeExceptionMessage.ADMIN_CHALLENGE_NOT_FOUND);
		} catch (ChallengeException e) {
			throw e;
		} catch (Exception e) {
			throw new ChallengeException(ChallengeExceptionMessage.ADMIN_CHALLENGE_UPDATE_FAILED);
		}
	}

	/**
	 * 챌린지 전시 상태를 수정합니다.
	 */
	public void updateChallengeDisplayStatus(Long challengeId, AdminChallengeDisplayStatusUpdateRequestDto request) {
		try {
			// PersonalChallenge인지 확인
			var personalChallenge = personalChallengeRepository.findById(challengeId);
			if (personalChallenge.isPresent()) {
				personalChallenge.get().updateDisplayStatus(request.displayStatus());
				return;
			}

			// TeamChallenge인지 확인
			var teamChallenge = teamChallengeRepository.findById(challengeId);
			if (teamChallenge.isPresent()) {
				teamChallenge.get().updateDisplayStatus(request.displayStatus());
				return;
			}

			// 둘 다 없으면 예외 발생
			throw new ChallengeException(ChallengeExceptionMessage.ADMIN_CHALLENGE_NOT_FOUND);
		} catch (ChallengeException e) {
			throw e;
		} catch (Exception e) {
			throw new ChallengeException(ChallengeExceptionMessage.ADMIN_CHALLENGE_UPDATE_FAILED);
		}
	}

	public CursorTemplate<Long, AdminPersonalChallengeListResponseDto> getPersonalChallenges(Long cursor) {
		return personalChallengeRepository.findAllForAdminByCursor(cursor, DEFAULT_PAGE_SIZE);
	}

	public CursorTemplate<Long, AdminTeamChallengeListResponseDto> getTeamChallenges(Long cursor) {
		return teamChallengeRepository.findAllForAdminByCursor(cursor, DEFAULT_PAGE_SIZE);
	}

	/**
	 * 챌린지 상세 정보를 조회합니다.
	 */
	public AdminChallengeDetailResponseDto getChallengeDetail(Long challengeId) {
		var personalChallenge = personalChallengeRepository.findById(challengeId);
		if (personalChallenge.isPresent()) {
			return AdminChallengeDetailResponseDto.from(personalChallenge.get());
		}
		var teamChallenge = teamChallengeRepository.findById(challengeId);
		if (teamChallenge.isPresent()) {
			return AdminChallengeDetailResponseDto.from(teamChallenge.get());
		}

		throw new ChallengeException(ChallengeExceptionMessage.ADMIN_CHALLENGE_NOT_FOUND);
	}

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

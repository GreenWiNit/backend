package com.example.green.domain.challenge.service;

import java.time.LocalDateTime;
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
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeGroupRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.domain.challengecert.entity.TeamChallengeGroupParticipation;
import com.example.green.domain.challengecert.repository.PersonalChallengeParticipationRepository;
import com.example.green.domain.challengecert.repository.TeamChallengeGroupParticipationRepository;
import com.example.green.domain.challengecert.repository.dao.ChallengeParticipantDao;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.api.page.CursorTemplate;

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

	/**
	 * 챌린지를 생성합니다. (이미지는 별도 API로 처리)
	 */
	public Long createChallenge(AdminChallengeCreateRequestDto request) {
		try {
			String challengeCode = generateChallengeCode(request.challengeType());
			PointAmount point = PointAmount.of(request.challengePoint().longValue());

			// 챌린지 타입에 따른 분기 처리
			return switch (request.challengeType()) {
				case PERSONAL -> createPersonalChallenge(challengeCode, request, point);
				case TEAM -> createTeamChallenge(challengeCode, request, point);
				default -> throw new ChallengeException(ChallengeExceptionMessage.ADMIN_INVALID_CHALLENGE_TYPE);
			};
		} catch (ChallengeException e) {
			throw e; // ChallengeException은 그대로 전파
		} catch (Exception e) {
			throw new ChallengeException(ChallengeExceptionMessage.ADMIN_CHALLENGE_CREATE_FAILED);
		}
	}

	private Long createPersonalChallenge(String challengeCode, AdminChallengeCreateRequestDto request,
		PointAmount point) {
		PersonalChallenge challenge = PersonalChallenge.create(
			challengeCode,
			request.challengeName(),
			ChallengeStatus.PROCEEDING, // 기본값
			point,
			request.beginDateTime(),
			request.endDateTime(),
			request.challengeImageUrl(),
			request.challengeContent(),
			request.displayStatus()
		);
		return personalChallengeRepository.save(challenge).getId();
	}

	private Long createTeamChallenge(String challengeCode, AdminChallengeCreateRequestDto request, PointAmount point) {
		// maxGroupCount가 없으면 기본값 5 사용
		Integer maxGroupCount = request.maxGroupCount() != null ? request.maxGroupCount() : 5;

		TeamChallenge challenge = TeamChallenge.create(
			challengeCode,
			request.challengeName(),
			ChallengeStatus.PROCEEDING, // 기본값
			point,
			request.beginDateTime(),
			request.endDateTime(),
			maxGroupCount,
			request.challengeImageUrl(),
			request.challengeContent(),
			request.displayStatus()
		);
		return teamChallengeRepository.save(challenge).getId();
	}

	private String generateChallengeCode(ChallengeType challengeType) {
		return CodeGenerator.generateChallengeCode(challengeType, LocalDateTime.now());
	}

	/**
	 * 챌린지 정보를 수정합니다.
	 */
	public void updateChallenge(Long challengeId, AdminChallengeUpdateRequestDto request) {
		try {
			// PersonalChallenge인지 확인
			var personalChallenge = personalChallengeRepository.findById(challengeId);
			if (personalChallenge.isPresent()) {
				personalChallenge.get().update(
					request.challengeName(),
					PointAmount.of(request.challengePoint().longValue()),
					request.beginDateTime(),
					request.endDateTime(),
					request.challengeContent()
				);
				return;
			}

			// TeamChallenge인지 확인
			var teamChallenge = teamChallengeRepository.findById(challengeId);
			if (teamChallenge.isPresent()) {
				teamChallenge.get().update(
					request.challengeName(),
					PointAmount.of(request.challengePoint().longValue()),
					request.beginDateTime(),
					request.endDateTime(),
					request.challengeContent(),
					request.maxGroupCount()
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
		return personalChallengeRepository.findById(challengeId)
			.map(AdminChallengeDetailResponseDto::from)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.ADMIN_CHALLENGE_NOT_FOUND));
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

		List<TeamChallengeGroupParticipation> participants = teamChallengeGroupParticipationRepository.findAll()
			.stream()
			.filter(p -> p.getTeamChallengeGroup().equals(group))
			.toList();

		return AdminTeamChallengeGroupDetailResponseDto.from(group, participants);
	}
} 
package com.example.green.domain.challenge.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupUpdateRequestDto;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.entity.vo.GroupAddress;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.TeamChallengeGroupRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.domain.challengecert.entity.TeamChallengeGroupParticipation;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.challengecert.entity.enums.GroupRoleType;
import com.example.green.domain.challengecert.repository.TeamChallengeGroupParticipationRepository;
import com.example.green.domain.challengecert.repository.TeamChallengeParticipationRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamChallengeGroupService {

	private static final int DEFAULT_PAGE_SIZE = 20;

	private final TeamChallengeGroupRepository teamChallengeGroupRepository;
	private final TeamChallengeRepository teamChallengeRepository;
	private final TeamChallengeParticipationRepository teamChallengeParticipationRepository;
	private final TeamChallengeGroupParticipationRepository teamChallengeGroupParticipationRepository;
	private final MemberRepository memberRepository;
	private final TimeUtils timeUtils;

	/**
	 * 팀 챌린지 그룹 목록을 조회합니다. (페이지 사이즈: 20개 고정)
	 */
	public CursorTemplate<Long, TeamChallengeGroupListResponseDto> getTeamChallengeGroups(
		Long challengeId,
		Long cursor,
		Long memberId
	) {
		return teamChallengeGroupRepository.findGroupsByChallengeIdAndCursor(
			challengeId, cursor, DEFAULT_PAGE_SIZE, memberId
		);
	}

	/**
	 * 팀 챌린지 그룹 상세 정보를 조회합니다.
	 */
	public TeamChallengeGroupDetailResponseDto getTeamChallengeGroupDetail(
		Long groupId,
		Long memberId
	) {
		TeamChallengeGroup group = teamChallengeGroupRepository.findById(groupId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.CHALLENGE_GROUP_NOT_FOUND));

		Boolean isLeader = isUserLeaderOfGroup(groupId, memberId);
		Boolean isParticipant = isUserParticipantOfGroup(groupId, memberId);

		String groupAddressString;
		if (group.getGroupAddress() != null) {
			groupAddressString = group.getGroupAddress().getFullAddress();
		} else {
			groupAddressString = null;
		}

		return new TeamChallengeGroupDetailResponseDto(
			group.getId(),
			group.getGroupName(),
			groupAddressString,
			group.getGroupDescription(),
			group.getOpenChatUrl(),
			group.getGroupBeginDateTime(),
			group.getGroupEndDateTime(),
			group.getCurrentParticipants(),
			group.getMaxParticipants(),
			group.getGroupStatus(),
			isLeader,
			isParticipant
		);
	}

	/**
	 * 팀 챌린지 그룹을 생성하고 생성자를 리더로 등록합니다.
	 */
	@Transactional
	public Long createTeamChallengeGroup(
		Long challengeId,
		TeamChallengeGroupCreateRequestDto request,
		Long memberId
	) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.MEMBER_NOT_FOUND));

		TeamChallenge teamChallenge = teamChallengeRepository.findById(challengeId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_FOUND));

		// 팀 챌린지에 참가 중인지 확인
		TeamChallengeParticipation participation = teamChallengeParticipationRepository
			.findByTeamChallengeAndMember(teamChallenge, member)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING_IN_CHALLENGE));

		// 그룹 주소 생성
		GroupAddress groupAddress = GroupAddress.of(request.roadAddress(), request.detailAddress());

		// 팀 코드 생성
		String teamCode = CodeGenerator.generateTeamGroupCode(LocalDateTime.now());

		// 그룹 생성
		TeamChallengeGroup group = TeamChallengeGroup.create(
			teamCode,
			request.groupName(),
			request.groupBeginDateTime(),
			request.groupEndDateTime(),
			request.maxParticipants(),
			groupAddress,
			request.groupDescription(),
			request.openChatUrl(),
			teamChallenge
		);

		TeamChallengeGroup savedGroup = teamChallengeGroupRepository.save(group);

		// 생성자를 리더로 그룹에 참가
		TeamChallengeGroupParticipation groupParticipation = TeamChallengeGroupParticipation.create(
			participation,
			savedGroup,
			GroupRoleType.LEADER
		);
		teamChallengeGroupParticipationRepository.save(groupParticipation);

		return savedGroup.getId();
	}

	/**
	 * 팀 챌린지 그룹에 참가합니다.
	 */
	@Transactional
	public void joinTeamChallengeGroup(Long groupId, Long memberId) {
		// 공통 검증: 그룹 존재 여부 및 챌린지 참가 여부 확인
		ValidationResult validationResult = validateUserParticipationInChallenge(groupId, memberId);
		TeamChallengeGroup group = validationResult.group();
		TeamChallengeParticipation participation = validationResult.participation();

		// 이미 그룹에 참가 중인지 확인
		boolean alreadyParticipating = teamChallengeGroupParticipationRepository
			.existsByTeamChallengeParticipationAndTeamChallengeGroup(participation, group);
		if (alreadyParticipating) {
			throw new ChallengeException(ChallengeExceptionMessage.ALREADY_PARTICIPATING_IN_GROUP);
		}

		// 그룹 참가 가능 여부 확인
		LocalDateTime now = timeUtils.now();
		if (!group.canParticipate(now)) {
			throw new ChallengeException(ChallengeExceptionMessage.CANNOT_PARTICIPATE_IN_GROUP);
		}

		// 그룹에 참가
		TeamChallengeGroupParticipation groupParticipation = TeamChallengeGroupParticipation.create(
			participation,
			group,
			GroupRoleType.MEMBER
		);
		teamChallengeGroupParticipationRepository.save(groupParticipation);
	}

	/**
	 * 팀 챌린지 그룹 정보를 수정합니다. (리더만 가능)
	 */
	@Transactional
	public void updateTeamChallengeGroup(
		Long groupId,
		TeamChallengeGroupUpdateRequestDto request,
		Long memberId
	) {
		// 공통 검증: 그룹 존재 여부 및 챌린지 참가 여부 확인
		ValidationResult validationResult = validateUserParticipationInChallenge(groupId, memberId);
		TeamChallengeGroup group = validationResult.group();

		// 리더 권한 확인
		if (!isUserLeaderOfGroup(groupId, memberId)) {
			throw new ChallengeException(ChallengeExceptionMessage.NOT_GROUP_LEADER);
		}

		// 그룹 주소 생성
		GroupAddress groupAddress = GroupAddress.of(request.roadAddress(), request.detailAddress());

		// 그룹 정보 수정
		group.update(
			request.groupName(),
			groupAddress,
			request.groupDescription(),
			request.openChatUrl(),
			request.groupBeginDateTime(),
			request.groupEndDateTime(),
			request.maxParticipants()
		);
	}

	/**
	 * 팀 챌린지 그룹을 삭제합니다. (리더만 가능)
	 */
	@Transactional
	public void deleteTeamChallengeGroup(Long groupId, Long memberId) {
		// 공통 검증: 그룹 존재 여부 및 챌린지 참가 여부 확인
		ValidationResult validationResult = validateUserParticipationInChallenge(groupId, memberId);
		TeamChallengeGroup group = validationResult.group();

		// 리더 권한 확인
		if (!isUserLeaderOfGroup(groupId, memberId)) {
			throw new ChallengeException(ChallengeExceptionMessage.NOT_GROUP_LEADER);
		}

		// 그룹 참가자들 정리
		teamChallengeGroupParticipationRepository.deleteByTeamChallengeGroup(group);

		// 그룹 삭제
		teamChallengeGroupRepository.delete(group);
	}

	/**
	 * 사용자가 해당 그룹의 챌린지에 참가 중인지 검증합니다.
	 *
	 * @param groupId 그룹 ID
	 * @param memberId 사용자 ID
	 * @return 검증 결과 (그룹, 멤버, 참가 정보)
	 * @throws ChallengeException 그룹이 존재하지 않거나, 멤버가 존재하지 않거나, 챌린지에 참가하지 않은 경우
	 */
	private ValidationResult validateUserParticipationInChallenge(Long groupId, Long memberId) {
		// 1. 그룹 조회
		TeamChallengeGroup group = teamChallengeGroupRepository.findById(groupId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.CHALLENGE_GROUP_NOT_FOUND));

		// 2. 멤버 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.MEMBER_NOT_FOUND));

		// 3. 팀 챌린지 참가 확인
		TeamChallengeParticipation participation = teamChallengeParticipationRepository
			.findByTeamChallengeAndMember(group.getTeamChallenge(), member)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING_IN_CHALLENGE));

		return new ValidationResult(group, member, participation);
	}

	private Boolean isUserLeaderOfGroup(Long groupId, Long memberId) {
		if (memberId == null) {
			return false;
		}

		return teamChallengeGroupParticipationRepository
			.existsByTeamChallengeGroupIdAndTeamChallengeParticipationMemberIdAndGroupRoleType(
				groupId, memberId, GroupRoleType.LEADER
			);
	}

	private Boolean isUserParticipantOfGroup(Long groupId, Long memberId) {
		if (memberId == null) {
			return false;
		}

		return teamChallengeGroupParticipationRepository
			.existsByTeamChallengeGroupIdAndTeamChallengeParticipationMemberId(groupId, memberId);
	}

	/**
	 * 공통 검증 결과를 담는 내부 레코드 클래스
	 */
	private record ValidationResult(
		TeamChallengeGroup group,
		Member member,
		TeamChallengeParticipation participation
	) {
	}
}

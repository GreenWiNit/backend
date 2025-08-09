package com.example.green.domain.challenge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.ChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.ChallengeParticipationStatus;
import com.example.green.domain.challenge.entity.BaseChallenge;
import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.PersonalChallengeParticipationRepository;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeParticipationRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.challengecert.repository.TeamChallengeGroupParticipationRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChallengeService {

	private static final int DEFAULT_PAGE_SIZE = 20;

	private final PersonalChallengeRepository personalChallengeRepository;
	private final TeamChallengeRepository teamChallengeRepository;
	private final PersonalChallengeParticipationRepository personalChallengeParticipationRepository;
	private final TeamChallengeParticipationRepository teamChallengeParticipationRepository;
	private final TeamChallengeGroupParticipationRepository teamChallengeGroupParticipationRepository;
	private final MemberRepository memberRepository;
	private final TimeUtils timeUtils;

	// 내가 참여한 팀 챌린지 목록 조회
	public CursorTemplate<Long, ChallengeListResponseDto> getMyTeamChallenges(Long cursor,
		PrincipalDetails currentUser) {
		Member member = getMemberById(currentUser.getMemberId());
		return teamChallengeParticipationRepository
			.findMyParticipationsByCursor(member, cursor, DEFAULT_PAGE_SIZE);
	}

	// 챌린지 상세 조회
	public ChallengeDetailResponseDto getChallengeDetail(Long challengeId, PrincipalDetails currentUser) {
		BaseChallenge challenge = findChallengeById(challengeId);
		ChallengeParticipationStatus participationStatus = determineParticipationStatus(challenge, currentUser);
		return ChallengeDetailResponseDto.from(challenge, participationStatus);
	}

	// 챌린지 참여
	@Transactional
	public void joinChallenge(Long challengeId, PrincipalDetails currentUser) {
		BaseChallenge challenge = findChallengeById(challengeId);
		Member member = getMemberById(currentUser.getMemberId());
		validateChallengeParticipation(challenge, member);

		if (challenge instanceof PersonalChallenge personalChallenge) {
			joinPersonalChallenge(personalChallenge, member);
		} else if (challenge instanceof TeamChallenge teamChallenge) {
			joinTeamChallenge(teamChallenge, member);
		}
	}

	// 챌린지 탈퇴
	@Transactional
	public void leaveChallenge(Long challengeId, PrincipalDetails currentUser) {
		BaseChallenge challenge = findChallengeById(challengeId);
		Member member = getMemberById(currentUser.getMemberId());

		if (challenge instanceof PersonalChallenge personalChallenge) {
			leavePersonalChallenge(personalChallenge, member);
		} else if (challenge instanceof TeamChallenge teamChallenge) {
			leaveTeamChallenge(teamChallenge, member);
		}
	}

	private BaseChallenge findChallengeById(Long challengeId) {
		return personalChallengeRepository.findById(challengeId)
			.map(pc -> (BaseChallenge)pc)
			.orElseGet(() -> teamChallengeRepository.findById(challengeId)
				.map(tc -> (BaseChallenge)tc)
				.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_FOUND)));
	}

	private Member getMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND));
	}

	private ChallengeParticipationStatus determineParticipationStatus(
		BaseChallenge challenge,
		PrincipalDetails currentUser
	) {
		if (currentUser == null) {
			return ChallengeParticipationStatus.NOT_LOGGED_IN;
		}

		Member member = getMemberById(currentUser.getMemberId());
		boolean isParticipating = false;

		if (challenge instanceof PersonalChallenge personalChallenge) {
			isParticipating = personalChallengeParticipationRepository
				.existsByMemberAndPersonalChallenge(member, personalChallenge);
		} else if (challenge instanceof TeamChallenge teamChallenge) {
			isParticipating = teamChallengeParticipationRepository
				.existsByMemberAndTeamChallenge(member, teamChallenge);
		}

		return isParticipating
			? ChallengeParticipationStatus.JOINED
			: ChallengeParticipationStatus.NOT_JOINED;
	}

	private void validateChallengeParticipation(BaseChallenge challenge, Member member) {
		if (!challenge.canParticipate(timeUtils.now())) {
			throw new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_PARTICIPATABLE);
		}

		boolean isParticipating = false;
		if (challenge instanceof PersonalChallenge personalChallenge) {
			isParticipating = personalChallengeParticipationRepository
				.existsByMemberAndPersonalChallenge(member, personalChallenge);
		} else if (challenge instanceof TeamChallenge teamChallenge) {
			isParticipating = teamChallengeParticipationRepository
				.existsByMemberAndTeamChallenge(member, teamChallenge);
		}

		if (isParticipating) {
			throw new ChallengeException(ChallengeExceptionMessage.ALREADY_PARTICIPATING);
		}
	}

	private void joinPersonalChallenge(PersonalChallenge challenge, Member member) {
		PersonalChallengeParticipation participation = PersonalChallengeParticipation.create(
			challenge,
			member,
			timeUtils.now()
		);
		personalChallengeParticipationRepository.save(participation);
	}

	private void joinTeamChallenge(TeamChallenge challenge, Member member) {
		TeamChallengeParticipation participation = TeamChallengeParticipation.create(
			challenge,
			member,
			timeUtils.now()
		);
		teamChallengeParticipationRepository.save(participation);
	}

	private void leavePersonalChallenge(PersonalChallenge challenge, Member member) {
		PersonalChallengeParticipation participation = personalChallengeParticipationRepository
			.findByMemberAndPersonalChallenge(member, challenge)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING));

		if (!challenge.canParticipate(timeUtils.now())) {
			throw new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_LEAVEABLE);
		}

		personalChallengeParticipationRepository.delete(participation);
	}

	private void leaveTeamChallenge(TeamChallenge challenge, Member member) {
		TeamChallengeParticipation participation = teamChallengeParticipationRepository
			.findByMemberAndTeamChallenge(member, challenge)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING));

		if (!challenge.canParticipate(timeUtils.now())) {
			throw new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_LEAVEABLE);
		}

		// 그룹에 참여 중인 상태에서는 팀 챌린지 탈퇴 불가
		if (teamChallengeGroupParticipationRepository.existsByTeamChallengeParticipation(participation)) {
			throw new ChallengeException(ChallengeExceptionMessage.CANNOT_LEAVE_WHILE_IN_GROUP);
		}

		// TeamChallengeParticipation 삭제
		teamChallengeParticipationRepository.delete(participation);
	}
}

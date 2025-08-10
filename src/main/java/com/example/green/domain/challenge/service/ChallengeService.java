package com.example.green.domain.challenge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.entity.BaseChallenge;
import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.PersonalChallengeParticipationRepository;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeParticipationRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.challengecert.repository.TeamChallengeGroupParticipationRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChallengeService {

	private final PersonalChallengeRepository personalChallengeRepository;
	private final TeamChallengeRepository teamChallengeRepository;
	private final PersonalChallengeParticipationRepository personalChallengeParticipationRepository;
	private final TeamChallengeParticipationRepository teamChallengeParticipationRepository;
	private final TeamChallengeGroupParticipationRepository teamChallengeGroupParticipationRepository;
	private final MemberRepository memberRepository;
	private final PersonalChallengeQuery personalChallengeQuery;
	private final TeamChallengeQuery teamChallengeQuery;
	private final TimeUtils timeUtils;

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

	@Transactional
	public void joinPersonalChallenge(Long challengeId, Long memberId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.addParticipation(memberId, timeUtils.now());
	}

	@Transactional
	public void joinTeamChallenge(Long challengeId, Long memberId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.addParticipation(memberId, timeUtils.now());
	}

	private void leavePersonalChallenge(PersonalChallenge challenge, Member member) {
		PersonalChallengeParticipation participation = personalChallengeParticipationRepository
			.findByMemberIdAndPersonalChallenge(member.getId(), challenge)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING));

		if (!challenge.canParticipate(timeUtils.now())) {
			throw new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_LEAVEABLE);
		}

		personalChallengeParticipationRepository.delete(participation);
	}

	private void leaveTeamChallenge(TeamChallenge challenge, Member member) {
		TeamChallengeParticipation participation = teamChallengeParticipationRepository
			.findByMemberIdAndTeamChallenge(member.getId(), challenge)
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

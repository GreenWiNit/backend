package com.example.green.domain.certification.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.certification.application.command.PersonalChallengeCertificateCommand;
import com.example.green.domain.certification.application.command.TeamChallengeCertificateCommand;
import com.example.green.domain.certification.domain.ChallengeCertification;
import com.example.green.domain.certification.domain.ChallengeCertificationQuery;
import com.example.green.domain.certification.domain.ChallengeCertificationRepository;
import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.domain.MemberSnapshot;
import com.example.green.domain.certification.exception.CertificationException;
import com.example.green.domain.certification.exception.CertificationExceptionMessage;
import com.example.green.domain.certification.util.CertificationClientHelper;
import com.example.green.global.utils.TimeUtils;
import com.example.green.infra.client.dto.ChallengeGroupDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChallengeCertificationService {

	private final ChallengeCertificationRepository challengeCertificationRepository;
	private final ChallengeCertificationQuery challengeCertificationQuery;
	private final CertificationClientHelper certificationClientHelper;
	private final TimeUtils timeUtils;

	public void certificatePersonalChallenge(PersonalChallengeCertificateCommand cmd) {
		validateFutureCertification(cmd.challengeDate());
		challengeCertificationQuery.checkAlreadyPersonalCert(cmd.challengeId(), cmd.challengeDate(), cmd.memberId());

		ChallengeSnapshot challenge = certificationClientHelper.getPersonalSnapshot(cmd.challengeId(), cmd.memberId());
		MemberSnapshot member = certificationClientHelper.getMemberSnapshot(cmd.memberId());
		ChallengeCertification certification = cmd.toEntity(member, challenge);

		challengeCertificationRepository.save(certification);
		certificationClientHelper.processCertSideEffect(certification.getImageUrl());
	}

	public void certificateTeamChallenge(TeamChallengeCertificateCommand cmd) {
		ChallengeGroupDto group = certificationClientHelper.getChallengeGroupDto(cmd.groupId(), cmd.memberId());
		validateFutureCertification(group.challengeDate());
		validateChallengeCompleted(group.afterDateTime());
		challengeCertificationQuery.checkAlreadyTeamCert(group.challengeId(), group.challengeDate(), cmd.memberId());

		ChallengeSnapshot challenge = certificationClientHelper.getTeamSnapshot(group.challengeId(), group.groupCode());
		MemberSnapshot member = certificationClientHelper.getMemberSnapshot(cmd.memberId());
		ChallengeCertification certification = cmd.toEntity(member, challenge, group.challengeDate());

		challengeCertificationRepository.save(certification);
		certificationClientHelper.processCertSideEffect(certification.getImageUrl(), cmd.groupId(), cmd.memberId());
	}

	public void approve(List<Long> certificationIds) {
		List<ChallengeCertification> approvedCerts = challengeCertificationRepository.findAllById(certificationIds)
			.stream()
			.filter(ChallengeCertification::canApprove)
			.peek(ChallengeCertification::approve)
			.toList();

		certificationClientHelper.processApproveSideEffect(approvedCerts);
	}

	public void reject(List<Long> certificationIds) {
		challengeCertificationRepository.findAllById(certificationIds)
			.forEach(ChallengeCertification::reject);
	}

	private void validateFutureCertification(LocalDate challengeDate) {
		if (challengeDate.isAfter(timeUtils.nowLocalDate())) {
			throw new CertificationException(CertificationExceptionMessage.FUTURE_DATE_NOT_ALLOWED);
		}
	}

	private void validateChallengeCompleted(LocalDateTime afterDateTime) {
		if (afterDateTime.isAfter(timeUtils.now())) {
			throw new CertificationException(CertificationExceptionMessage.CHALLENGE_NOT_COMPLETED_YET);
		}
	}
}

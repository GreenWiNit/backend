package com.example.green.domain.certification.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.certification.application.command.TeamChallengeCertificateCommand;
import com.example.green.domain.certification.domain.ChallengeCertification;
import com.example.green.domain.certification.domain.ChallengeCertificationQuery;
import com.example.green.domain.certification.domain.ChallengeCertificationRepository;
import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.domain.MemberSnapshot;
import com.example.green.domain.certification.ui.dto.ChallengeGroupDto;
import com.example.green.domain.certification.util.CertificationClientHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeCertificationService {

	private final ChallengeCertificationRepository challengeCertificationRepository;
	private final ChallengeCertificationQuery challengeCertificationQuery;
	private final CertificationClientHelper certificationClientHelper;

	public void certificateTeamChallenge(TeamChallengeCertificateCommand cmd) {
		ChallengeGroupDto group = certificationClientHelper.getChallengeGroupDto(cmd.groupId(), cmd.memberId());
		challengeCertificationQuery.checkAlreadyTeamCert(group.challengeId(), group.challengeDate(), cmd.memberId());

		ChallengeSnapshot challenge = certificationClientHelper.getTeamSnapshot(group.challengeId(), group.groupCode());
		MemberSnapshot member = certificationClientHelper.getMemberSnapshot(cmd.memberId());
		ChallengeCertification certification = cmd.toEntity(member, challenge, group.challengeDate());

		challengeCertificationRepository.save(certification);
		certificationClientHelper.processCertSideEffect(certification.getImageUrl());
	}
}

package com.example.green.domain.certification.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.certification.application.command.TeamChallengeCertificateCommand;
import com.example.green.domain.certification.domain.ChallengeCertification;
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
	private final CertificationClientHelper certificationClientHelper;

	public void certificateTeamChallenge(TeamChallengeCertificateCommand cmd) {
		ChallengeGroupDto group = certificationClientHelper.getChallengeGroupDto(cmd.groupId(), cmd.memberId());
		ChallengeSnapshot challenge = certificationClientHelper.getTeamSnapshot(group.challengeId(), group.groupCode());
		MemberSnapshot member = certificationClientHelper.getMemberSnapshot(cmd.memberId());
		ChallengeCertification certification =
			ChallengeCertification.create(member, challenge, cmd.imageUrl(), cmd.review(), group.challengeDate());
		challengeCertificationRepository.save(certification);
	}
}

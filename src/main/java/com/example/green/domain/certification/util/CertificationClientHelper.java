package com.example.green.domain.certification.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.certification.domain.ChallengeCertification;
import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.domain.MemberSnapshot;
import com.example.green.domain.common.constants.PointTransactionType;
import com.example.green.infra.client.ChallengeClient;
import com.example.green.infra.client.FileClient;
import com.example.green.infra.client.MemberClient;
import com.example.green.infra.client.PointClient;
import com.example.green.infra.client.dto.ChallengeDto;
import com.example.green.infra.client.dto.ChallengeGroupDto;
import com.example.green.infra.client.dto.MemberDto;
import com.example.green.infra.client.request.CertificationConfirmRequest;
import com.example.green.infra.client.request.PointEarnRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CertificationClientHelper {

	private final ChallengeClient challengeClient;
	private final MemberClient memberClient;
	private final FileClient fileClient;
	private final PointClient pointClient;

	public ChallengeSnapshot getPersonalSnapshot(Long challengeId, Long memberId, LocalDate challengeDate) {
		ChallengeDto dto = challengeClient.getPersonalChallengeByMemberAndDate(challengeId, memberId, challengeDate);
		return ChallengeSnapshot.ofPersonal(dto.id(), dto.name(), dto.code(), dto.point(), dto.imageUrl());
	}

	public ChallengeSnapshot getTeamSnapshot(Long challengeId, String groupCode) {
		ChallengeDto dto = challengeClient.getTeamChallenge(challengeId);
		return ChallengeSnapshot.ofTeam(dto.id(), dto.name(), dto.code(), dto.point(), groupCode, dto.imageUrl());
	}

	public ChallengeGroupDto getChallengeGroupDto(Long groupId, Long memberId) {
		return challengeClient.getChallengeGroup(groupId, memberId);
	}

	public MemberSnapshot getMemberSnapshot(Long memberId) {
		MemberDto dto = memberClient.getMember(memberId);
		return MemberSnapshot.of(dto.getId(), dto.getMemberKey());
	}

	public void processCertSideEffect(String imageUrl) {
		fileClient.confirmUsingImage(imageUrl);
	}

	public void processApproveSideEffect(List<ChallengeCertification> certs) {
		List<PointEarnRequest> pointRequests = certs.stream()
			.map(CertificationClientHelper::convertPointEarnRequest)
			.toList();
		pointClient.earnPoints(pointRequests);

		List<CertificationConfirmRequest> teamConfirmRequests = certs.stream()
			.filter(cert -> ChallengeSnapshot.TEAM_TYPE.equals(cert.getChallenge().getType()))
			.map(this::convertTeamConfirmRequest)
			.toList();
		if (!teamConfirmRequests.isEmpty()) {
			challengeClient.confirmTeamCertifications(teamConfirmRequests);
		}
	}

	private static PointEarnRequest convertPointEarnRequest(ChallengeCertification cert) {
		return new PointEarnRequest(
			cert.getMember().getMemberId(),
			BigDecimal.valueOf(cert.getChallenge().getChallengePoint()),
			cert.getChallenge().getChallengeId(),
			cert.getChallenge().getChallengeName() + " 완료",
			PointTransactionType.CHALLENGE,
			LocalDateTime.of(cert.getCertifiedDate(), LocalTime.MIN)
		);
	}

	private CertificationConfirmRequest convertTeamConfirmRequest(ChallengeCertification cert) {
		return new CertificationConfirmRequest(
			cert.getMember().getMemberId(),
			cert.getChallenge().getGroupCode()
		);
	}
}

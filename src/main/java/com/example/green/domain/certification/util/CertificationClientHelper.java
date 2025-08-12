package com.example.green.domain.certification.util;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.domain.MemberSnapshot;
import com.example.green.domain.common.service.FileManager;
import com.example.green.global.client.ChallengeClient;
import com.example.green.global.client.MemberClient;
import com.example.green.global.client.dto.ChallengeDto;
import com.example.green.global.client.dto.ChallengeGroupDto;
import com.example.green.global.client.dto.MemberDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CertificationClientHelper {

	private final ChallengeClient challengeClient;
	private final MemberClient memberClient;
	private final FileManager fileManager;

	public ChallengeSnapshot getPersonalSnapshot(Long challengeId, Long memberId, LocalDate challengeDate) {
		ChallengeDto dto = challengeClient.getPersonalChallengeByMemberAndDate(challengeId, memberId, challengeDate);
		return ChallengeSnapshot.ofPersonal(dto.id(), dto.name(), dto.code());
	}

	public ChallengeSnapshot getTeamSnapshot(Long challengeId, String groupCode) {
		ChallengeDto dto = challengeClient.getTeamChallenge(challengeId);
		return ChallengeSnapshot.ofTeam(dto.id(), dto.name(), dto.code(), groupCode);
	}

	public ChallengeGroupDto getChallengeGroupDto(Long groupId, Long memberId) {
		return challengeClient.getChallengeGroup(groupId, memberId);
	}

	public MemberSnapshot getMemberSnapshot(Long memberId) {
		MemberDto dto = memberClient.getMember(memberId);
		return MemberSnapshot.of(dto.getId(), dto.getMemberKey());
	}

	public void processCertSideEffect(String imageUrl) {
		fileManager.confirmUsingImage(imageUrl);
	}
}

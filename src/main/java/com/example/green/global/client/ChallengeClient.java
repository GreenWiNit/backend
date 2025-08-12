package com.example.green.global.client;

import java.time.LocalDate;

import com.example.green.global.client.dto.ChallengeDto;
import com.example.green.global.client.dto.ChallengeGroupDto;

public interface ChallengeClient {

	ChallengeDto getTeamChallenge(Long challengeId);

	ChallengeDto getPersonalChallengeByMemberAndDate(Long challengeId, Long memberId, LocalDate challengeDate);

	ChallengeGroupDto getChallengeGroup(Long groupId, Long memberId);
}

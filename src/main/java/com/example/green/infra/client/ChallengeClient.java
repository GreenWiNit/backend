package com.example.green.infra.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.example.green.infra.client.dto.ChallengeDto;
import com.example.green.infra.client.dto.ChallengeGroupDto;
import com.example.green.infra.client.request.CertificationConfirmRequest;

public interface ChallengeClient {

	ChallengeDto getTeamChallenge(Long challengeId);

	ChallengeDto getPersonalChallengeByMemberAndDate(Long challengeId, Long memberId, LocalDate challengeDate);

	ChallengeGroupDto getChallengeGroup(Long groupId, Long memberId);

	void confirmTeamCertification(CertificationConfirmRequest request);

	/**
	 * 회원별 총 챌린지 인증 횟수 조회
	 * @param memberIds 회원 ID 목록
	 * @return Map<회원ID, 총 인증 횟수>
	 */
	Map<Long, Long> getCertificationCountByMembers(List<Long> memberIds);
}

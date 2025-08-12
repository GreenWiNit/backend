package com.example.green.domain.challenge.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.green.domain.certification.domain.ChallengeCertification;
import com.example.green.global.client.MemberClient;
import com.example.green.global.client.PointClient;
import com.example.green.global.client.dto.MemberDto;
import com.example.green.global.client.request.PointEarnRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClientHelper {

	private final MemberClient memberClient;
	private final PointClient pointClient;

	public String requestMemberKey(Long memberId) {
		return memberClient.getMember(memberId).getMemberKey();
	}

	public List<String> requestMemberKeys(List<Long> memberIds) {
		return memberClient.getMembers(memberIds)
			.stream()
			.map(MemberDto::getMemberKey)
			.toList();
	}

	public Map<Long, String> requestMemberKeyById(List<Long> memberIds) {
		return memberClient.getMemberByIds(memberIds)
			.entrySet()
			.stream()
			.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getMemberKey()));
	}

	public void processApproveSideEffect(List<ChallengeCertification> certs) {
		List<PointEarnRequest> request = certs.stream()
			.map(cert -> new PointEarnRequest(
				cert.getMember().getMemberId(),
				BigDecimal.valueOf(cert.getChallenge().getChallengePoint()),
				cert.getChallenge().getChallengeId(),
				cert.getChallenge().getChallengeName()
			))
			.toList();
		pointClient.earnPoints(request);
	}
}

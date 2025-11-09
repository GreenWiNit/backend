package com.example.green.domain.certification.infra;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.green.domain.certification.domain.ChallengeCertificationRepository;
import com.example.green.domain.certification.infra.executor.ChallengeCertificationQueryExecutor;
import com.example.green.infra.client.CertificationClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CertificationAdapter implements CertificationClient {

	private final ChallengeCertificationRepository challengeCertificationRepository;
	private final ChallengeCertificationQueryExecutor executor;

	@Override
	public int getTotalCertifiedCountByMember(Long memberId) {
		return challengeCertificationRepository.countChallengeCertificationByMemberMemberId(memberId);
	}

	@Override
	public Map<Long, Long> getCertificationCountByMembers(List<Long> memberIds) {
		return executor.executeCertificationCountByMembersQuery(memberIds);
	}
}

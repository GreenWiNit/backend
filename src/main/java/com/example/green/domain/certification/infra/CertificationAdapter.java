package com.example.green.domain.certification.infra;

import org.springframework.stereotype.Component;

import com.example.green.domain.certification.domain.ChallengeCertificationQuery;
import com.example.green.infra.client.CertificationClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CertificationAdapter implements CertificationClient {

	private final ChallengeCertificationQuery challengeCertificationQuery;

	@Override
	public int getTotalCertifiedCountByMember(Long memberId) {
		return challengeCertificationQuery.getTotalCertifiedCountByMember(memberId);
	}
}

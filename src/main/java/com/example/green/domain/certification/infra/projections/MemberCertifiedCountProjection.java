package com.example.green.domain.certification.infra.projections;

public interface MemberCertifiedCountProjection {
	Long getMemberId();

	int getCertifiedCount();
}

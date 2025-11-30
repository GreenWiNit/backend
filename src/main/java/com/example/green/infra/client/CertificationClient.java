package com.example.green.infra.client;

import java.util.List;
import java.util.Map;

public interface CertificationClient {

	int getTotalCertifiedCountByMember(Long memberId);

	Map<Long, Long> getCertificationCountByMembers(List<Long> memberIds);

}

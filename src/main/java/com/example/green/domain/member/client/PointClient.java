package com.example.green.domain.member.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PointClient {

	void earnPoints(Long memberId, String detail, BigDecimal amount);

	Map<Long, BigDecimal> getEarnedPointByMember(List<Long> memberIds);
}

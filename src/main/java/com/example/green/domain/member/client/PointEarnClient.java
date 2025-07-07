package com.example.green.domain.member.client;

import java.math.BigDecimal;

public interface PointEarnClient {

	void earnPoints(Long memberId, String detail, BigDecimal amount);
}

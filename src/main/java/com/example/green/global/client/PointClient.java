package com.example.green.global.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.example.green.global.client.request.PointEarnRequest;
import com.example.green.global.client.request.PointSpendRequest;

public interface PointClient {

	void earnPoints(List<PointEarnRequest> requests);

	Map<Long, BigDecimal> getEarnedPointByMember(List<Long> memberIds);

	void spendPoints(PointSpendRequest pointSpendRequest);

	BigDecimal getTotalPoints(Long userId);
}

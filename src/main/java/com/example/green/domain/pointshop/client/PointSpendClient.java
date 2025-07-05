package com.example.green.domain.pointshop.client;

import com.example.green.domain.pointshop.client.dto.PointSpendRequest;

public interface PointSpendClient {

	void spendPoints(PointSpendRequest pointSpendRequest);
}

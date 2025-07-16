package com.example.green.domain.pointshop.order.client;

import com.example.green.domain.pointshop.order.client.dto.PointSpendRequest;

public interface PointSpendClient {

	void spendPoints(PointSpendRequest pointSpendRequest);
}

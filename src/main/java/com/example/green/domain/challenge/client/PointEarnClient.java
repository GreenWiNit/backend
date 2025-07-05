package com.example.green.domain.challenge.client;

import com.example.green.domain.challenge.client.request.PointEarnRequest;

public interface PointEarnClient {

	void earnPoints(PointEarnRequest dto);
}

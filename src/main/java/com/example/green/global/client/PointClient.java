package com.example.green.global.client;

import java.util.List;

import com.example.green.global.client.request.PointEarnRequest;

public interface PointClient {

	void earnPoint(PointEarnRequest dto);

	void earnPoints(List<PointEarnRequest> requests);
}

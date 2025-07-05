package com.example.green.domain.point.adapter;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.client.PointEarnClient;
import com.example.green.domain.challenge.client.request.PointEarnRequest;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.entity.vo.TargetType;
import com.example.green.domain.point.service.PointTransactionService;
import com.example.green.domain.pointshop.client.PointSpendClient;
import com.example.green.domain.pointshop.client.dto.PointSpendRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointTransactionAdaptor implements PointSpendClient, PointEarnClient {

	private final PointTransactionService pointTransactionService;

	@Override
	public void spendPoints(PointSpendRequest dto) {
		PointSource pointSource = PointSource.ofTarget(dto.targetId(), dto.reason(), TargetType.EXCHANGE);
		PointAmount amount = PointAmount.of(dto.amount());
		pointTransactionService.spendPoints(dto.memberId(), amount, pointSource);
	}

	@Override
	public void earnPoints(PointEarnRequest dto) {
		PointSource pointSource = PointSource.ofTarget(dto.targetId(), dto.reason(), TargetType.CHALLENGE);
		PointAmount amount = PointAmount.of(dto.amount());
		pointTransactionService.earnPoints(dto.memberId(), amount, pointSource);
	}
}

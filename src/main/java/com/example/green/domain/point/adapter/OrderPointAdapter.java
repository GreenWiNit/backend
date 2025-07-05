package com.example.green.domain.point.adapter;

import org.springframework.stereotype.Component;

import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.entity.vo.TargetType;
import com.example.green.domain.point.service.PointTransactionService;
import com.example.green.domain.pointshop.client.PointSpendClient;
import com.example.green.domain.pointshop.client.dto.PointSpendRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderPointAdapter implements PointSpendClient {

	private final PointTransactionService pointTransactionService;

	@Override
	public void spendPoints(PointSpendRequest dto) {
		PointSource pointSource = PointSource.ofTarget(dto.targetId(), dto.reason(), TargetType.ORDER);
		PointAmount amount = PointAmount.of(dto.amount());
		pointTransactionService.spendPoints(dto.memberId(), amount, pointSource);
	}
}

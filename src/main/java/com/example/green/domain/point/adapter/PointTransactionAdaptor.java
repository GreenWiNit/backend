package com.example.green.domain.point.adapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.client.PointEarnClient;
import com.example.green.domain.challenge.client.request.PointEarnRequest;
import com.example.green.domain.member.client.PointClient;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.entity.vo.TargetType;
import com.example.green.domain.point.repository.PointTransactionQueryRepository;
import com.example.green.domain.point.service.PointTransactionService;
import com.example.green.domain.pointshop.client.PointSpendClient;
import com.example.green.domain.pointshop.client.dto.PointSpendRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointTransactionAdaptor implements PointSpendClient, PointEarnClient, PointClient {

	private final PointTransactionService pointTransactionService;
	private final PointTransactionQueryRepository pointTransactionQueryRepository;

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

	@Override
	public void earnPoints(Long memberId, String detail, BigDecimal amount) {
		PointSource pointSource = PointSource.ofEvent(detail);
		PointAmount pointAmount = PointAmount.of(amount);
		pointTransactionService.earnPoints(memberId, pointAmount, pointSource);
	}

	@Override
	public Map<Long, BigDecimal> getEarnedPointByMember(List<Long> memberIds) {
		return pointTransactionQueryRepository.findEarnedPointByMember(memberIds);
	}
}

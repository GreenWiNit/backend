package com.example.green.domain.point.infra.adapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.entity.vo.TargetType;
import com.example.green.domain.point.repository.PointTransactionQueryRepository;
import com.example.green.domain.point.service.PointTransactionService;
import com.example.green.infra.client.PointClient;
import com.example.green.infra.client.request.PointEarnRequest;
import com.example.green.infra.client.request.PointSpendRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointTransactionAdaptor implements PointClient {

	private final PointTransactionService pointTransactionService;
	private final PointTransactionQueryRepository pointTransactionQueryRepository;

	@Override
	public void spendPoints(PointSpendRequest dto) {
		PointSource pointSource = PointSource.ofTarget(dto.targetId(), dto.description(), TargetType.EXCHANGE);
		PointAmount amount = PointAmount.of(dto.amount());
		pointTransactionService.spendPoints(dto.memberId(), amount, pointSource);
	}

	@Override
	public void earnPoints(List<PointEarnRequest> requests) {
		for (PointEarnRequest dto : requests) {
			TargetType type = TargetType.from(dto.type());
			PointSource pointSource = PointSource.ofTarget(dto.targetId(), dto.description(), type);
			PointAmount amount = PointAmount.of(dto.amount());
			pointTransactionService.earnPoints(dto.memberId(), amount, pointSource);
		}
	}

	@Override
	public Map<Long, BigDecimal> getEarnedPointByMember(List<Long> memberIds) {
		return pointTransactionQueryRepository.findEarnedPointByMember(memberIds);
	}

	@Override
	public BigDecimal getTotalPoints(Long userId) {
		return pointTransactionService.getPointAmount(userId).getAmount();
	}
}

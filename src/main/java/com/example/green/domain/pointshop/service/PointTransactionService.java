package com.example.green.domain.pointshop.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.green.domain.pointshop.entity.point.PointTransaction;
import com.example.green.domain.pointshop.entity.point.vo.PointAmount;
import com.example.green.domain.pointshop.entity.point.vo.PointSource;
import com.example.green.domain.pointshop.exception.point.PointException;
import com.example.green.domain.pointshop.exception.point.PointExceptionMessage;
import com.example.green.domain.pointshop.repository.PointTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointTransactionService {

	private final PointTransactionRepository pointTransactionRepository;

	public void spendPoints(Long memberId, PointAmount spendAmount, PointSource pointSource) {
		PointAmount currentAmount = pointTransactionRepository.findLatestBalance(memberId)
			.orElseGet(() -> PointAmount.of(BigDecimal.ZERO));
		if (!currentAmount.canSpend(spendAmount)) {
			throw new PointException(PointExceptionMessage.NOT_ENOUGH_POINT);
		}
		PointTransaction spend = PointTransaction.spend(memberId, pointSource, spendAmount, currentAmount);
		pointTransactionRepository.save(spend);
	}
}

package com.example.green.domain.point.service;

import org.springframework.stereotype.Service;

import com.example.green.domain.point.entity.PointTransaction;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.repository.PointTransactionRepository;
import com.example.green.domain.pointshop.exception.point.PointException;
import com.example.green.domain.pointshop.exception.point.PointExceptionMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointTransactionService {

	private final PointTransactionRepository pointTransactionRepository;

	public void spendPoints(Long memberId, PointAmount spendAmount, PointSource pointSource) {
		PointAmount currentAmount = pointTransactionRepository.findLatestBalance(memberId)
			.orElseThrow(() -> new PointException(PointExceptionMessage.NO_POINTS_ACCUMULATED));
		if (!currentAmount.canSpend(spendAmount)) {
			throw new PointException(PointExceptionMessage.NOT_ENOUGH_POINT);
		}

		PointTransaction spend = PointTransaction.spend(memberId, pointSource, spendAmount, currentAmount);
		pointTransactionRepository.save(spend);
	}

	public void earnPoints(Long memberId, PointAmount earnAmount, PointSource pointSource) {
		PointAmount currentAmount = pointTransactionRepository.findLatestBalance(memberId)
			.orElseGet(PointAmount::ofZero);
		PointTransaction earn = PointTransaction.earn(memberId, pointSource, earnAmount, currentAmount);
		pointTransactionRepository.save(earn);
	}
}

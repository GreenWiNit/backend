package com.example.green.domain.point.service;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.point.entity.PointTransaction;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.exception.PointException;
import com.example.green.domain.point.exception.PointExceptionMessage;
import com.example.green.domain.point.repository.PointTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PointTransactionService {

	private final PointTransactionRepository pointTransactionRepository;

	@Retryable(retryFor = CannotAcquireLockException.class, backoff = @Backoff(delay = 50))
	public void spendPoints(Long memberId, PointAmount spendAmount, PointSource pointSource) {
		PointAmount currentAmount = getPointAmount(memberId);
		if (!currentAmount.canSpend(spendAmount)) {
			throw new PointException(PointExceptionMessage.NOT_ENOUGH_POINT);
		}

		PointTransaction spend = PointTransaction.spend(memberId, pointSource, spendAmount, currentAmount);
		pointTransactionRepository.save(spend);
	}

	@Retryable(retryFor = CannotAcquireLockException.class, backoff = @Backoff(delay = 50))
	public void earnPoints(Long memberId, PointAmount earnAmount, PointSource pointSource) {
		PointAmount currentAmount = getPointAmount(memberId);
		PointTransaction earn = PointTransaction.earn(memberId, pointSource, earnAmount, currentAmount);
		pointTransactionRepository.save(earn);
	}

	public PointAmount getPointAmount(Long memberId) {
		return pointTransactionRepository.findLatestBalance(memberId)
			.orElseGet(PointAmount::ofZero);
	}
}

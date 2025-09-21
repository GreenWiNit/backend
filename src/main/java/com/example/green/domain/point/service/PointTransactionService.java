package com.example.green.domain.point.service;

import java.time.LocalDateTime;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
@Transactional(isolation = Isolation.SERIALIZABLE)
public class PointTransactionService {

	private final PointTransactionRepository pointTransactionRepository;

	@Retryable(retryFor = CannotAcquireLockException.class, backoff = @Backoff(delay = 50))
	public void spendPoints(
		Long memberId, PointAmount spendAmount, PointSource pointSource, LocalDateTime transactionAt
	) {
		PointAmount currentAmount = getPointAmount(memberId);
		validateCanSpend(spendAmount, currentAmount);

		PointTransaction spend =
			PointTransaction.spend(memberId, pointSource, spendAmount, currentAmount, transactionAt);
		pointTransactionRepository.save(spend);
	}

	private static void validateCanSpend(PointAmount spendAmount, PointAmount currentAmount) {
		if (!currentAmount.canSpend(spendAmount)) {
			throw new PointException(PointExceptionMessage.NOT_ENOUGH_POINT);
		}
	}

	@Retryable(retryFor = CannotAcquireLockException.class, backoff = @Backoff(delay = 50))
	public void earnPoints(
		Long memberId, PointAmount earnAmount, PointSource pointSource, LocalDateTime transactionAt
	) {
		PointAmount currentAmount = getPointAmount(memberId);
		PointTransaction earn = PointTransaction.earn(memberId, pointSource, earnAmount, currentAmount, transactionAt);
		pointTransactionRepository.save(earn);
	}

	public PointAmount getPointAmount(Long memberId) {
		return pointTransactionRepository.findFirstByMemberIdOrderByIdDesc(memberId)
			.map(PointTransaction::getBalanceAfter)
			.orElse(PointAmount.ofZero());
	}
}

package com.example.green.domain.common.lock;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.green.global.utils.ThreadUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockManager {

	private static final int MAX_RETRY_COUNT = 3;
	private static final Duration DEFAULT_LOCK_TIMEOUT = Duration.ofSeconds(10);
	private static final long RETRY_DELAY_MS = 100L;
	private static final String FAILED_FORMAT = "%d회 재시도 후 락 획득 실패: %s";

	private final DistributedLockRepository lockRepository;
	private final PlatformTransactionManager transactionManager;
	private final ThreadUtils threadUtils;
	private final Clock clock;

	public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
		return executeWithLock(lockKey, DEFAULT_LOCK_TIMEOUT, supplier);
	}

	public <T> T executeWithLock(String lockKey, Duration lockTimeout, Supplier<T> supplier) {
		if (enterCriticalZone(lockKey, lockTimeout)) {
			return executeImmediately(lockKey, supplier);
		}
		return executeWithRetry(lockKey, supplier);
	}

	private <T> T executeWithRetry(String lockKey, Supplier<T> supplier) {
		for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
			if (!threadUtils.sleepQuietly(RETRY_DELAY_MS)) {
				break;
			}
			if (enterCriticalZone(lockKey, DEFAULT_LOCK_TIMEOUT)) {
				return executeImmediately(lockKey, supplier);
			}
		}

		throw new IllegalStateException(String.format(FAILED_FORMAT, MAX_RETRY_COUNT, lockKey));
	}

	private <T> T executeImmediately(String lockKey, Supplier<T> supplier) {
		try {
			return supplier.get();
		} finally {
			exitCriticalZone(lockKey);
		}
	}

	private boolean enterCriticalZone(String lockKey, Duration timeout) {
		try {
			return executeInNewTransaction(() -> {
				String owner = threadUtils.getCurrentThreadName();
				DistributedLock lock = DistributedLock.create(lockKey, LocalDateTime.now(clock), timeout, owner);
				lockRepository.save(lock);
				log.debug("임계구역 진입 완료 : {} (진행)", lockKey);
				return true;
			});
		} catch (DataIntegrityViolationException e) {
			log.debug("임계 구역 진입 실패: {} (대기중)", lockKey);
			return false;
		}
	}

	private void exitCriticalZone(String lockKey) {
		executeInNewTransaction(() -> {
			lockRepository.deleteById(lockKey);
			log.debug("임계 구역 종료: {}", lockKey);
			return null;
		});
	}

	private <T> T executeInNewTransaction(Supplier<T> supplier) {
		try {
			TransactionTemplate template = new TransactionTemplate(transactionManager);
			template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			return template.execute(status -> supplier.get());
		} catch (Exception e) {
			log.warn("락 획득 중 예상치 못한 오류: ", e);
			throw e;
		}
	}
}

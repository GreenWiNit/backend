package com.example.green.domain.common.lock;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockManager {

	private final DistributedLockRepository lockRepository;
	private final PlatformTransactionManager transactionManager;

	public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
		try {
			enterCriticalZone(lockKey);
			return supplier.get();
		} catch (Exception e) {
			log.debug("임계 구역 진입 실패: {} (대기중)", lockKey);
			throw new IllegalStateException();
		} finally {
			exitCriticalZone(lockKey);
		}
	}

	private void enterCriticalZone(String lockKey) {
		executeInNewTransaction(() -> {
			DistributedLock lock = new DistributedLock(lockKey);
			lockRepository.save(lock);
		});
		log.debug("임계구역 진입 완료 : {} (진행)", lockKey);
	}

	private void exitCriticalZone(String lockKey) {
		try {
			executeInNewTransaction(() -> lockRepository.deleteById(lockKey));
			log.debug("임계 구역 종료: {}", lockKey);
		} catch (Exception e) {
			log.debug("락이 이미 삭제됨: {}", lockKey);
		}
	}

	private void executeInNewTransaction(Runnable runnable) {
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		template.execute(status -> {
			runnable.run();
			return null;
		});
	}
}

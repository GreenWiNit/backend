package com.example.integration.common.concurrency;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcurrencyTestExecutor {

	public ConcurrencyTestResult executeInParallel(Runnable task, ConcurrencyTestConfig config)
		throws InterruptedException {
		return executeInParallel(() -> {
			task.run();
			return true;
		}, config);
	}

	public ConcurrencyTestResult executeInParallel(
		Supplier<Boolean> task,
		ConcurrencyTestConfig config
	) throws InterruptedException {

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		// 두 개의 CountDownLatch 사용
		CountDownLatch startSignal = new CountDownLatch(1);    // 시작 신호
		CountDownLatch doneSignal = new CountDownLatch(config.getThreadCount()); // 완료 신호

		long startTime = System.currentTimeMillis();

		try (ExecutorService executor = Executors.newFixedThreadPool(config.getThreadCount())) {

			// 모든 스레드를 먼저 생성하고 대기 상태로 만듦
			for (int i = 0; i < config.getThreadCount(); i++) {
				final int threadId = i;
				executor.submit(() -> {
					try {
						log.debug("Thread-{} 시작 신호 대기 중...", threadId);

						// 모든 스레드가 여기서 대기
						startSignal.await();

						log.debug("Thread-{} 동시 실행 시작!", threadId);

						// 실제 작업 수행
						executeTask(task, successCount, failureCount, threadId);

					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						failureCount.incrementAndGet();
						log.warn("Thread-{} 인터럽트됨", threadId);
					} finally {
						doneSignal.countDown();
					}
				});
			}

			// 잠시 대기 (모든 스레드가 준비 상태)
			Thread.sleep(50);

			// 모든 스레드 동시 시작
			log.debug("=== 모든 스레드 동시 시작 신호! ===");
			startSignal.countDown();

			// 모든 스레드 완료 대기
			boolean completed = doneSignal.await(config.getTimeoutSeconds(), TimeUnit.SECONDS);
			long executionTime = System.currentTimeMillis() - startTime;

			if (!completed) {
				log.warn("테스트 타임아웃 발생: {} ({}초)", config.getTestName(), config.getTimeoutSeconds());
			}

			return new ConcurrencyTestResult(
				config.getTestName(),
				successCount.get(),
				failureCount.get(),
				config.getThreadCount(),
				completed,
				executionTime
			);
		}
	}

	public <T> ConcurrencyTestResult executeWithParameters(
		Function<T, Boolean> task,
		List<T> parameters,
		ConcurrencyTestConfig config
	) throws InterruptedException {

		validateParameters(parameters, config);

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		// 동시성 보장을 위한 두 개의 래치
		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal = new CountDownLatch(config.getThreadCount());

		long startTime = System.currentTimeMillis();

		try (ExecutorService executor = Executors.newFixedThreadPool(config.getThreadCount())) {

			// 모든 스레드를 파라미터와 함께 준비
			for (int i = 0; i < parameters.size(); i++) {
				final T parameter = parameters.get(i);
				final int threadId = i;

				executor.submit(() -> {
					try {
						log.debug("Thread-{} (param: {}) 시작 신호 대기 중...", threadId, parameter);

						// 모든 스레드가 여기서 대기
						startSignal.await();

						log.debug("Thread-{} (param: {}) 동시 실행 시작!", threadId, parameter);

						// 실제 작업 수행
						executeParameterTask(task, parameter, successCount, failureCount, threadId);

					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						failureCount.incrementAndGet();
						log.warn("Thread-{} 인터럽트됨", threadId);
					} finally {
						doneSignal.countDown();
					}
				});
			}

			// 잠시 대기 (모든 스레드가 준비 상태가 되도록)
			Thread.sleep(50);

			// 모든 스레드 동시 시작!
			log.debug("=== 모든 스레드({})개 동시 시작 신호 ===", parameters.size());
			startSignal.countDown();

			// 완료 대기
			boolean completed = doneSignal.await(config.getTimeoutSeconds(), TimeUnit.SECONDS);
			long executionTime = System.currentTimeMillis() - startTime;

			return new ConcurrencyTestResult(
				config.getTestName(),
				successCount.get(),
				failureCount.get(),
				config.getThreadCount(),
				completed,
				executionTime
			);
		}
	}

	private void executeTask(
		Supplier<Boolean> task,
		AtomicInteger successCount,
		AtomicInteger failureCount,
		int threadId
	) {
		try {
			log.debug("Thread-{} 작업 실행 중...", threadId);

			Boolean result = task.get();

			if (Boolean.TRUE.equals(result)) {
				successCount.incrementAndGet();
				log.debug("Thread-{} 성공!", threadId);
			} else {
				failureCount.incrementAndGet();
				log.debug("Thread-{} 실패 (false 반환)", threadId);
			}
		} catch (Exception e) {
			failureCount.incrementAndGet();
			log.debug("Thread-{} 예외 발생: {}", threadId, e.getMessage());
		}
	}

	private <T> void executeParameterTask(
		Function<T, Boolean> task,
		T parameter,
		AtomicInteger successCount,
		AtomicInteger failureCount,
		int threadId
	) {
		try {
			log.debug("Thread-{} 파라미터 작업 실행 중: {}", threadId, parameter);

			Boolean result = task.apply(parameter);

			if (Boolean.TRUE.equals(result)) {
				successCount.incrementAndGet();
				log.debug("Thread-{} 성공! (param: {})", threadId, parameter);
			} else {
				failureCount.incrementAndGet();
				log.debug("Thread-{} 실패 (false 반환, param: {})", threadId, parameter);
			}
		} catch (Exception e) {
			failureCount.incrementAndGet();
			log.debug("Thread-{} 예외 발생 (param: {}): {}", threadId, parameter, e.getMessage());
		}
	}

	private <T> void validateParameters(List<T> parameters, ConcurrencyTestConfig config) {
		if (parameters.size() != config.getThreadCount()) {
			throw new IllegalArgumentException(
				String.format("파라미터 개수(%d)와 스레드 개수(%d)가 일치하지 않습니다.",
					parameters.size(), config.getThreadCount())
			);
		}
	}
}
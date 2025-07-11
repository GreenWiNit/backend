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
		CountDownLatch latch = new CountDownLatch(config.getThreadCount());

		long startTime = System.currentTimeMillis();

		try (ExecutorService executor = Executors.newFixedThreadPool(config.getThreadCount())) {
			for (int i = 0; i < config.getThreadCount(); i++) {
				executor.submit(() -> executeTask(task, successCount, failureCount, latch));
			}

			boolean completed = latch.await(config.getTimeoutSeconds(), TimeUnit.SECONDS);
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
		CountDownLatch latch = new CountDownLatch(config.getThreadCount());

		long startTime = System.currentTimeMillis();

		try (ExecutorService executor = Executors.newFixedThreadPool(config.getThreadCount())) {
			for (T parameter : parameters) {
				executor.submit(() -> executeParameterTask(task, parameter, successCount, failureCount, latch));
			}

			boolean completed = latch.await(config.getTimeoutSeconds(), TimeUnit.SECONDS);
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
		CountDownLatch latch
	) {
		try {
			Boolean result = task.get();
			if (Boolean.TRUE.equals(result)) {
				successCount.incrementAndGet();
			} else {
				failureCount.incrementAndGet();
			}
		} catch (Exception e) {
			failureCount.incrementAndGet();
			log.debug("Task execution failed", e);
		} finally {
			latch.countDown();
		}
	}

	private <T> void executeParameterTask(
		Function<T, Boolean> task,
		T parameter,
		AtomicInteger successCount,
		AtomicInteger failureCount,
		CountDownLatch latch
	) {
		try {
			Boolean result = task.apply(parameter);
			if (Boolean.TRUE.equals(result)) {
				successCount.incrementAndGet();
			} else {
				failureCount.incrementAndGet();
			}
		} catch (Exception e) {
			failureCount.incrementAndGet();
		} finally {
			latch.countDown();
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
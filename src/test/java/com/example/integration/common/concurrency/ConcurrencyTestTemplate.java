package com.example.integration.common.concurrency;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConcurrencyTestTemplate {

	private final ConcurrencyTestExecutor executor;

	private ConcurrencyTestTemplate() {
		this.executor = new ConcurrencyTestExecutor();
	}

	public ConcurrencyTestResult executeInParallel(
		Runnable task,
		ConcurrencyTestConfig config
	) throws InterruptedException {
		return executor.executeInParallel(task, config);
	}

	public ConcurrencyTestResult executeInParallel(
		Supplier<Boolean> task,
		ConcurrencyTestConfig config
	) throws InterruptedException {
		return executor.executeInParallel(task, config);
	}

	public <T> ConcurrencyTestResult executeWithParameters(
		Function<T, Boolean> task,
		List<T> parameters,
		ConcurrencyTestConfig config
	) throws InterruptedException {
		return executor.executeWithParameters(task, parameters, config);
	}

	public static ConcurrencyTestBuilder build() {
		return new ConcurrencyTestBuilder(new ConcurrencyTestTemplate());
	}
}

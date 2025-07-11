package com.example.integration.common.concurrency;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.example.integration.common.concurrency.ConcurrencyTestConfig.ConcurrencyTestConfigBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConcurrencyTestBuilder {

	private final ConcurrencyTestTemplate template;
	private final ConcurrencyTestConfigBuilder configBuilder = ConcurrencyTestConfig.builder();

	public ConcurrencyTestBuilder threadCount(int threadCount) {
		this.configBuilder.threadCount(threadCount);
		return this;
	}

	public ConcurrencyTestBuilder timeout(int timeoutSeconds) {
		this.configBuilder.timeoutSeconds(timeoutSeconds);
		return this;
	}

	public ConcurrencyTestBuilder testName(String testName) {
		this.configBuilder.testName(testName);
		return this;
	}

	public ConcurrencyTestResult execute(Runnable task) throws InterruptedException {
		return template.executeInParallel(task, configBuilder.build());
	}

	public ConcurrencyTestResult execute(Supplier<Boolean> task) throws InterruptedException {
		return template.executeInParallel(task, configBuilder.build());
	}

	public <T> ConcurrencyTestResult executeWithParams(
		Function<T, Boolean> task,
		List<T> parameters
	) throws InterruptedException {
		return template.executeWithParameters(task, parameters, configBuilder.build());
	}
}
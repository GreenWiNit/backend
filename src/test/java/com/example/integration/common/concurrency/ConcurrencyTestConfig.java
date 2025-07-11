package com.example.integration.common.concurrency;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConcurrencyTestConfig {
	@Builder.Default
	private final int threadCount = 2;

	@Builder.Default
	private final int timeoutSeconds = 10;

	@Builder.Default
	private final String testName = "동시성 테스트";
}
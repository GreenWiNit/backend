package com.example.integration.common.concurrency;

public record ConcurrencyTestResult(
	String testName,
	int successCount,
	int failureCount,
	int totalCount,
	boolean allTasksCompleted,
	long executionTimeMs
) {
	public boolean hasFailures() {
		return failureCount > 0;
	}

	public boolean allSucceeded() {
		return successCount == totalCount && allTasksCompleted;
	}

	public double getSuccessRate() {
		return totalCount == 0 ? 0.0 : (double)successCount / totalCount * 100;
	}

	@Override
	public String toString() {
		return String.format(
			"[%s] 성공=%d, 실패=%d, 전체=%d, 완료=%s, 실행시간=%dms, 성공률=%.1f%%",
			testName, successCount, failureCount, totalCount,
			allTasksCompleted, executionTimeMs, getSuccessRate()
		);
	}
}
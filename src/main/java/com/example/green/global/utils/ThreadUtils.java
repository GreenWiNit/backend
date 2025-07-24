package com.example.green.global.utils;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ThreadUtils {

	public void waitWithBackoff(long baseDelayMs, int attempt) {
		long delay = baseDelayMs * attempt + ThreadLocalRandom.current().nextLong(baseDelayMs);
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			log.error("스레드 대기 중 인터럽트 발생", e);
			Thread.currentThread().interrupt();
		}
	}
}
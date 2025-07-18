package com.example.green.global.utils;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ThreadUtils {

	public boolean sleepQuietly(long millis) {
		try {
			Thread.sleep(millis);
			return true;
		} catch (InterruptedException e) {
			log.error("스레드 대기 중 인터럽트 발생", e);
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public String getCurrentThreadName() {
		return Thread.currentThread().getName();
	}
}
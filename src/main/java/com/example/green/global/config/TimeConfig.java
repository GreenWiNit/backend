package com.example.green.global.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 시간 관련 설정
 * - 모든 환경에서 일관된 한국시간(Asia/Seoul) 제공
 * - 로컬/서버/CI 환경에 관계없이 동일한 시간대 사용
 */
@Configuration
public class TimeConfig {

	/**
	 * 한국시간(Asia/Seoul) 고정 Clock
	 * - 환경에 관계없이 항상 한국시간 제공
	 * - GitHub Actions, AWS 등에서도 일관된 동작 보장
	 */
	@Bean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}
}

package com.example.green.infra.database.config;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.green.infra.database.strategy.UpperSnakeNamingStrategy;

@EnableJpaAuditing(
	auditorAwareRef = "auditorAwareConfig",
	dateTimeProviderRef = "koreaDateTimeProvider"
)
@Configuration
public class JpaConfig {

	@Bean
	public PhysicalNamingStrategy physicalNamingStrategy() {
		return new UpperSnakeNamingStrategy();
	}

	/**
	 * 한국 시간 기준으로 현재 시간 제공 (JPA Auditing용)
	 */
	@Bean
	public DateTimeProvider koreaDateTimeProvider() {
		return () -> Optional.of(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
	}
}

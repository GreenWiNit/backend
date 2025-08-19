package com.example.green.infra.database.config;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.green.infra.database.strategy.UpperSnakeNamingStrategy;

@EnableJpaAuditing(
	auditorAwareRef = "auditorAwareConfig"
)
@Configuration
public class JpaConfig {

	@Bean
	public PhysicalNamingStrategy physicalNamingStrategy() {
		return new UpperSnakeNamingStrategy();
	}

	@Bean
	public DateTimeProvider koreaDateTimeProvider(Clock clock) {
		return () -> Optional.of(LocalDateTime.now(clock));
	}
}

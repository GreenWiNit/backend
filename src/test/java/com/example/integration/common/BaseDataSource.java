package com.example.integration.common;

import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseDataSource {

	protected final JdbcTemplate jdbcTemplate;

	public void createIdempotency() {
		jdbcTemplate.execute("""
				CREATE TABLE idempotencies (
						idempotency_key varchar(255) not null,
						response varchar(255) not null,
						primary key (idempotency_key)
					);
			""");
	}

	public void deleteIdempotency() {
		jdbcTemplate.execute("DROP TABLE IF EXISTS IDEMPOTENCIES");
	}
}

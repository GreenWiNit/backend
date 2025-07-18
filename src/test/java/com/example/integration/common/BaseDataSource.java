package com.example.integration.common;

import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseDataSource {

	protected final JdbcTemplate jdbcTemplate;

	public void createIdempotency() {
		jdbcTemplate.execute("""
			    CREATE TABLE IDEMPOTENCIES (
			        IDEMPOTENCY_KEY varchar(255) not null, 
			        RESPONSE varchar(255) not null, 
			        primary key (IDEMPOTENCY_KEY)
			    ) engine=InnoDB
			""");
	}

	public void deleteIdempotency() {
		jdbcTemplate.execute("DROP TABLE IF EXISTS IDEMPOTENCIES");
	}
}

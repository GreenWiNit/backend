package com.example.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration
public class MySqlTestContainerConfig {

	@Bean
	@ServiceConnection
	public MySQLContainer<?> mySQLContainer() {
		return new MySQLContainer<>("mysql:8.0");
	}
}

package com.example.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestContainerConfig {

    /* DB Migration MySQL -> PostgreSql
    @Bean
    @ServiceConnection
    public MySQLContainer<?> mySQLContainer() {
       return new MySQLContainer<>("mysql:8.0");
    }*/

	@Bean
	@ServiceConnection
	public PostgreSQLContainer<?> postgreSQLContainer() {
		return new PostgreSQLContainer<>("postgres:17.4");
	}
}
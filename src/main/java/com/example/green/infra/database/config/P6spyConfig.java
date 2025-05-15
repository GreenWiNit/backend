package com.example.green.infra.database.config;

import org.springframework.context.annotation.Configuration;

import com.example.green.infra.database.logging.CustomP6spySqlFormat;
import com.p6spy.engine.spy.P6SpyOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class P6spyConfig {

	@PostConstruct
	public void setLogMessageFormat() {
		P6SpyOptions.getActiveInstance()
			.setLogMessageFormat(CustomP6spySqlFormat.class.getName());
	}
}

package com.example.green.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServerEnvironmentLogger {

	private final Environment environment;

	@Value("${server.port:8080}")
	private String serverPort;

	public ServerEnvironmentLogger(Environment environment) {
		this.environment = environment;
	}

	@EventListener(WebServerInitializedEvent.class)
	public void onServerStarted(WebServerInitializedEvent event) {
		int actualPort = event.getWebServer().getPort();
		log.info("=== Server Environment Info ===");
		log.info("Actual Server Port: {}", actualPort);
		log.info("Configured Server Port: {}", serverPort);
		log.info("Active Profiles: {}", String.join(", ", environment.getActiveProfiles()));
		log.info("Default Profiles: {}", String.join(", ", environment.getDefaultProfiles()));
		log.info("===============================");
	}

	@EventListener(ApplicationReadyEvent.class)
	public void logEnvironmentVariables() {
		log.info("=== Environment Variables Check ===");
		log.info("OAUTH2_GOOGLE_CLIENT_ID: {}", 
			environment.getProperty("OAUTH2_GOOGLE_CLIENT_ID") != null ? "SET" : "NOT SET");
		log.info("OAUTH2_GOOGLE_REDIRECT_URI: {}", 
			environment.getProperty("OAUTH2_GOOGLE_REDIRECT_URI"));
		log.info("FRONTEND_URL: {}", environment.getProperty("FRONTEND_URL"));
		log.info("BACKEND_URL: {}", environment.getProperty("BACKEND_URL"));
		log.info("Spring Profile: {}", environment.getProperty("spring.profiles.active"));
		log.info("===================================");
	}
} 
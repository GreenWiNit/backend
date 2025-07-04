package com.example.green.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

	private final String frontendBaseUrl;

	public CorsMvcConfig(@Value("${app.frontend.base-url}") String frontendBaseUrl) {
		this.frontendBaseUrl = frontendBaseUrl;
	}

	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {

		corsRegistry.addMapping("/**")
			.allowedOrigins(frontendBaseUrl)
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
			.allowedHeaders("*")
			.allowCredentials(true)
			.exposedHeaders("Set-Cookie", "Authorization")
			.maxAge(3600);
	}
}


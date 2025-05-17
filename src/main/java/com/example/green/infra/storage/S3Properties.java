package com.example.green.infra.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class S3Properties {

	private final String bucket;
	private final String baseUrl;

	public S3Properties(
		@Value("${app.storage.bucket}") final String bucket,
		@Value("${app.storage.base-url}") final String baseUrl
	) {
		this.bucket = bucket;
		this.baseUrl = baseUrl;
	}

}

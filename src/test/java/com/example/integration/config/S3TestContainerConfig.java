package com.example.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class S3TestContainerConfig {

	@Bean(initMethod = "start", destroyMethod = "stop")
	public LocalStackContainer localStackContainer() {
		DockerImageName dockerImageName = DockerImageName.parse("localstack/localstack:3.0");
		LocalStackContainer localStackContainer = new LocalStackContainer(dockerImageName);
		localStackContainer.withServices(LocalStackContainer.Service.S3);
		return localStackContainer;
	}

	@Bean
	public S3Client testS3Client(LocalStackContainer localStackContainer) {
		S3Client s3Client = S3Client.builder()
			.endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create("test", "test")
			))
			.region(Region.of(localStackContainer.getRegion()))
			.build();

		s3Client.createBucket(b -> b.bucket("test-bucket"));

		return s3Client;
	}
}

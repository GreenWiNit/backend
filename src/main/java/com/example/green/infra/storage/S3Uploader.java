package com.example.green.infra.storage;

import org.springframework.stereotype.Component;

import com.example.green.domain.file.secondary.FileUploader;
import com.example.green.global.error.exception.StorageException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
public class S3Uploader implements FileUploader {

	private static final String IMAGE_KEY_SUFFIX = "images/";

	private final S3Properties s3Properties;
	private final S3Client s3Client;

	public String uploadImage(String key, byte[] content, String contentType) {
		try {
			String fullKey = IMAGE_KEY_SUFFIX + key;
			PutObjectRequest request = generatePutObjectRequest(fullKey, contentType);
			s3Client.putObject(request, RequestBody.fromBytes(content));

			return String.format("%s/%s", s3Properties.getBaseUrl(), fullKey);
		} catch (S3Exception e) {
			throw new StorageException(e);
		}
	}

	private PutObjectRequest generatePutObjectRequest(String key, String contentType) {
		return PutObjectRequest.builder()
			.bucket(s3Properties.getBucket())
			.key(key)
			.contentType(contentType)
			.build();
	}
}

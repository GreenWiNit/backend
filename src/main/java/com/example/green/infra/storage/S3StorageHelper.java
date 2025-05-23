package com.example.green.infra.storage;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.exception.StorageException;
import com.example.green.domain.file.outport.StorageHelper;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
public class S3StorageHelper implements StorageHelper {

	private static final String IMAGE_KEY_SUFFIX = "images/";

	private final S3Properties s3Properties;
	private final S3Client s3Client;

	@Override
	public void uploadImage(String key, MultipartFile imageFile) {
		try (InputStream inputStream = imageFile.getInputStream()) {
			String fullKey = IMAGE_KEY_SUFFIX + key;
			PutObjectRequest request = generatePutObjectRequest(fullKey, imageFile);
			s3Client.putObject(request, RequestBody.fromInputStream(inputStream, imageFile.getSize()));
		} catch (IOException | S3Exception e) {
			throw new StorageException(e);
		}
	}

	@Override
	public String getFullImageUrl(String key) {
		String fullKey = IMAGE_KEY_SUFFIX + key;
		return String.format("%s/%s", s3Properties.getBaseUrl(), fullKey);
	}

	private PutObjectRequest generatePutObjectRequest(String key, MultipartFile file) {
		return PutObjectRequest.builder()
			.bucket(s3Properties.getBucket())
			.key(key)
			.contentType(file.getContentType())
			.contentLength(file.getSize())
			.build();
	}
}

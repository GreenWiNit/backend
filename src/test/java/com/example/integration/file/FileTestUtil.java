package com.example.integration.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.green.domain.file.repository.FileJpaRepository;
import com.example.green.infra.storage.S3Properties;
import com.example.green.infra.storage.S3StorageHelper;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Component
public class FileTestUtil {

	@Autowired
	private S3StorageHelper storageHelper;
	@Autowired
	private S3Properties s3Properties;
	@Autowired
	private S3Client s3Client;
	@Autowired
	private FileJpaRepository fileJpaRepository;

	public boolean existsImageInS3(String imageUrl) {
		String imageKey = storageHelper.extractImageKey(imageUrl);
		try {
			s3Client.headObject(HeadObjectRequest.builder()
				.bucket(s3Properties.getBucket())
				.key(imageKey)
				.build());
			return true;
		} catch (NoSuchKeyException e) {
			return false;
		}
	}

	public boolean existsInImageMetadataInDb(String imageUrl) {
		String imageKey = storageHelper.extractImageKey(imageUrl);
		return fileJpaRepository.findByFileKey(imageKey)
			.isPresent();
	}

}

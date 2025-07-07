package com.example.integration.file;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.example.green.domain.file.repository.FileJpaRepository;
import com.example.green.infra.storage.S3Properties;
import com.example.green.infra.storage.S3StorageHelper;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@TestComponent
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

	public long countInImageMetadataInDb() {
		return fileJpaRepository.count();
	}

	public void createBucket() {
		if (!hasBucket(s3Properties.getBucket())) {
			s3Client.createBucket(req -> req.bucket(s3Properties.getBucket()));
		}
	}

	public void clearBucket() {
		if (hasBucket(s3Properties.getBucket())) {
			ListObjectsV2Request request = ListObjectsV2Request.builder()
				.bucket(s3Properties.getBucket())
				.build();

			deleteObjectsInBucket(request);

			s3Client.deleteBucket(req -> req.bucket(s3Properties.getBucket()));
		}
	}

	public void clearDatabase() {
		fileJpaRepository.deleteAll();
	}

	private boolean hasBucket(String bucket) {
		try {
			s3Client.headBucket(builder -> builder.bucket(bucket));
			return true;
		} catch (NoSuchBucketException e) {
			return false;
		}
	}

	private void deleteObjectsInBucket(ListObjectsV2Request request) {
		ListObjectsV2Response response = s3Client.listObjectsV2(request);
		List<ObjectIdentifier> objectsToDelete = response.contents().stream()
			.map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
			.toList();

		if (!objectsToDelete.isEmpty()) {
			DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
				.bucket(s3Properties.getBucket())
				.delete(Delete.builder().objects(objectsToDelete).build())
				.build();

			s3Client.deleteObjects(deleteRequest);
		}
	}
}

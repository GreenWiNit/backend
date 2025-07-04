package com.example.green.infra.storage;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;
import com.example.green.domain.file.service.StorageHelper;
import com.example.green.global.utils.IdUtils;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3StorageHelper implements StorageHelper {

	private static final String IMAGE_KEY_PREFIX = "images";
	private static final String FILE_KEY_FORMAT = "%s/%s/%s/%s/%s_%s%s";
	private static final String FILE_DATE_FORMAT = "yyyyMMdd";
	private static final int FILE_KEY_SUFFIX_LENGTH = 8;

	private final S3Properties s3Properties;
	private final S3Client s3Client;

	private final IdUtils idUtils;
	private final TimeUtils timeUtils;

	@Override
	public String uploadImage(String imageKey, MultipartFile imageFile) {
		try (InputStream inputStream = imageFile.getInputStream()) {
			PutObjectRequest request = generatePutObjectRequest(imageKey, imageFile);
			s3Client.putObject(request, RequestBody.fromInputStream(inputStream, imageFile.getSize()));
			return getFullImageUrl(imageKey);
		} catch (IOException | S3Exception e) {
			log.error("S3 업로드 중 예외 발생: ", e);
			throw new FileException(FileExceptionMessage.IMAGE_UPLOAD_FAILED);
		}
	}

	@Override
	public String getFullImageUrl(String imageKey) {
		return String.format("%s/%s", s3Properties.getBaseUrl(), imageKey);
	}

	@Override
	public String extractImageKey(String imageUrl) {
		String baseUrl = s3Properties.getBaseUrl();
		String imageUrlPrefix = String.format("%s/%s", baseUrl, IMAGE_KEY_PREFIX);
		if (!imageUrl.startsWith(imageUrlPrefix)) {
			throw new FileException(FileExceptionMessage.INVALID_IMAGE_URL);
		}
		return imageUrl.substring(baseUrl.length() + 1);
	}

	@Override
	public String generateFileKey(String purpose, String extension) {
		return String.format(FILE_KEY_FORMAT,
			IMAGE_KEY_PREFIX,
			purpose,
			idUtils.generateUniqueId(FILE_KEY_SUFFIX_LENGTH).substring(0, 2),
			timeUtils.getFormattedDate(FILE_DATE_FORMAT),
			idUtils.generateUniqueId(FILE_KEY_SUFFIX_LENGTH),
			timeUtils.getCurrentTimeMillis(),
			extension
		);
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

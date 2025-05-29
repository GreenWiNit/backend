package com.example.green.domain.file.utils;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;
import com.example.green.global.utils.UriValidator;

@Component
public class ImageValidator {

	private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png");
	private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png");

	private final UriValidator uriValidator;
	private final long maxImageSize;

	public ImageValidator(
		@Value("${spring.servlet.multipart.max-file-size}") final DataSize maxImageSize,
		final UriValidator uriValidator
	) {
		this.maxImageSize = maxImageSize.toBytes();
		this.uriValidator = uriValidator;
	}

	public void validate(MultipartFile imageFile) {
		validateImageFile(imageFile);
		String originalFilename = imageFile.getOriginalFilename();
		validateOriginalFileName(originalFilename);
		validateExtension(originalFilename.toLowerCase());
		validateImageContentType(imageFile.getContentType());
		validateImageSize(imageFile.getSize());
	}

	public void validateUrl(String imageUrl) {
		if (!uriValidator.isValidUri(imageUrl)) {
			throw new FileException(FileExceptionMessage.INVALID_IMAGE_URL);
		}
	}

	private void validateImageFile(MultipartFile imageFile) {
		if (imageFile == null || imageFile.isEmpty()) {
			throw new FileException(FileExceptionMessage.REQUIRED_IMAGE_FILE);
		}
	}

	private void validateOriginalFileName(String originalFilename) {
		if (originalFilename == null || originalFilename.trim().isEmpty()) {
			throw new FileException(FileExceptionMessage.EMPTY_IMAGE_FILE_NAME);
		}
	}

	private void validateExtension(String lowercaseImageName) {
		if (ALLOWED_IMAGE_EXTENSIONS.stream().noneMatch(lowercaseImageName::endsWith)) {
			throw new FileException(FileExceptionMessage.INVALID_IMAGE_TYPE);
		}
	}

	private void validateImageContentType(String contentType) {
		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
			throw new FileException(FileExceptionMessage.INVALID_IMAGE_TYPE);
		}
	}

	private void validateImageSize(long size) {
		if (size > maxImageSize) {
			throw new FileException(FileExceptionMessage.OVER_MAX_IMAGE_SIZE);
		}
	}
}

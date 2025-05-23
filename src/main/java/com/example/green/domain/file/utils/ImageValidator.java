package com.example.green.domain.file.utils;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;

@Component
public class ImageValidator {

	private static final String IMAGE_PREFIX = "image/";
	private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
		// 일반 이미지
		".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".tiff", ".tif",
		// 아이콘 관련
		".svg", ".ico", ".icon"
	);

	private final long maxImageSize;

	public ImageValidator(@Value("${spring.servlet.multipart.max-file-size}") final DataSize maxImageSize) {
		this.maxImageSize = maxImageSize.toBytes();
	}

	public void validateImageFile(MultipartFile imageFile) {
		String originalFilename = imageFile.getOriginalFilename();
		validateOriginalFileName(originalFilename);
		validateExtension(originalFilename.toLowerCase());
		validateImageContentType(imageFile.getContentType());
		validateImageSize(imageFile.getSize());
	}

	private void validateOriginalFileName(String originalFilename) {
		if (originalFilename == null || originalFilename.isEmpty()) {
			throw new FileException(FileExceptionMessage.EMPTY_IMAGE_FILE_NAME);
		}
	}

	private void validateExtension(String lowercaseImageName) {
		if (ALLOWED_IMAGE_EXTENSIONS.stream().noneMatch(lowercaseImageName::endsWith)) {
			throw new FileException(FileExceptionMessage.INVALID_IMAGE_TYPE);
		}
	}

	private void validateImageContentType(String contentType) {
		if (contentType == null || !contentType.startsWith(IMAGE_PREFIX)) {
			throw new FileException(FileExceptionMessage.INVALID_IMAGE_TYPE);
		}
	}

	private void validateImageSize(long size) {
		if (size > maxImageSize) {
			throw new FileException(FileExceptionMessage.OVER_MAX_IMAGE_SIZE);
		}
	}
}

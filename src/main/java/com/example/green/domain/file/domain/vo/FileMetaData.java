package com.example.green.domain.file.domain.vo;

import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FileMetaData {

	@Column(nullable = false)
	private String originalFilename;

	@Column(nullable = false)
	private String contentType;

	@Column(nullable = false)
	private Long contentLength;

	protected FileMetaData(String originalFilename, String contentType, long contentLength) {
		validateBasicMetaData(originalFilename, contentType, contentLength);
		this.originalFilename = originalFilename.trim();
		this.contentType = contentType.trim();
		this.contentLength = contentLength;
	}

	public static FileMetaData from(MultipartFile multipartFile) {
		return new FileMetaData(
			multipartFile.getOriginalFilename(),
			multipartFile.getContentType(),
			multipartFile.getSize()
		);
	}

	private static void validateBasicMetaData(String originalFilename, String contentType, long contentLength) {
		if (originalFilename == null || originalFilename.isBlank()) {
			throw new FileException(FileExceptionMessage.INVALID_FILE_METADATA);
		}
		if (contentType == null || contentType.isBlank()) {
			throw new FileException(FileExceptionMessage.INVALID_FILE_METADATA);
		}
		if (contentLength <= 0) {
			throw new FileException(FileExceptionMessage.INVALID_FILE_METADATA);
		}
	}

	public String getExtension() {
		int index = originalFilename.lastIndexOf(".");
		if (index <= 0 || index == originalFilename.length() - 1) {
			return "";
		}
		return originalFilename.substring(index).toLowerCase();
	}
}

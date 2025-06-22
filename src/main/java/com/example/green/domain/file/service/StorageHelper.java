package com.example.green.domain.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageHelper {

	String uploadImage(String key, MultipartFile imageFile);

	String getFullImageUrl(String key);

	String extractImageKey(String imageUrl);

	String generateFileKey(String purpose, String extension);
}

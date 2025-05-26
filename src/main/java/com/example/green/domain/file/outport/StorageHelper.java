package com.example.green.domain.file.outport;

import org.springframework.web.multipart.MultipartFile;

public interface StorageHelper {

	void uploadImage(String key, MultipartFile imageFile);

	String getFullImageUrl(String key);

	String extractImageKey(String imageUrl);
}

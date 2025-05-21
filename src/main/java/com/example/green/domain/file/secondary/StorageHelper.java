package com.example.green.domain.file.secondary;

public interface StorageHelper {

	void uploadImage(String key, byte[] content, String contentType);

	String getFullUrl(String key);
}

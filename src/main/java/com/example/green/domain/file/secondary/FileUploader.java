package com.example.green.domain.file.secondary;

public interface FileUploader {

	String uploadImage(String key, byte[] content, String contentType);
}

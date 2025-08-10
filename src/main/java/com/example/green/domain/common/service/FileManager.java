package com.example.green.domain.common.service;

public interface FileManager {

	void confirmUsingImage(String imageUrl);

	void unUseImage(String imageUrl);

	void swapImage(String beforeImageUrl, String afterImageUrl);
}

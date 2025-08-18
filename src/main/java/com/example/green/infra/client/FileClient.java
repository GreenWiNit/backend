package com.example.green.infra.client;

public interface FileClient {

	void confirmUsingImage(String imageUrl);

	void unUseImage(String imageUrl);

	void swapImage(String beforeImageUrl, String afterImageUrl);
}

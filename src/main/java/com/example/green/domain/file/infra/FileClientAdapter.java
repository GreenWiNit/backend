package com.example.green.domain.file.infra;

import org.springframework.stereotype.Component;

import com.example.green.domain.file.service.FileService;
import com.example.green.infra.client.FileClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileClientAdapter implements FileClient {

	private final FileService fileService;

	@Override
	public void confirmUsingImage(String imageUrl) {
		fileService.confirmUsingImage(imageUrl);
	}

	@Override
	public void unUseImage(String imageUrl) {
		fileService.unUseImage(imageUrl);
	}

	@Override
	public void swapImage(String beforeImageUrl, String afterImageUrl) {
		fileService.swapImage(beforeImageUrl, afterImageUrl);
	}
}

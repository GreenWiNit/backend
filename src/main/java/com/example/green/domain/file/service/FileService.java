package com.example.green.domain.file.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;
import com.example.green.domain.file.outport.StorageHelper;
import com.example.green.domain.file.utils.FileKeyGenerator;
import com.example.green.domain.file.utils.ImageValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

	private final StorageHelper storageHelper;
	private final ImageValidator imageValidator;
	private final FileKeyGenerator fileKeyGenerator;

	public String uploadImage(MultipartFile imageFile) {
		imageValidator.validate(imageFile);
		String originalFilename = imageFile.getOriginalFilename();
		String imageKey = fileKeyGenerator.generate("domain", originalFilename);
		processUpload(imageKey, imageFile);

		return storageHelper.getFullImageUrl(imageKey);
	}

	private void processUpload(String imageKey, MultipartFile imageFile) {
		try {
			storageHelper.uploadImage(imageKey, imageFile);
		} catch (RuntimeException exception) {
			log.error("s3 uploading failed: {}", exception.getMessage());
			throw new FileException(FileExceptionMessage.IMAGE_UPLOAD_FAILED);
		}
	}
}

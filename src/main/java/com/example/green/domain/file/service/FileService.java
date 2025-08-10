package com.example.green.domain.file.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.file.domain.FileEntity;
import com.example.green.domain.file.domain.vo.FileMetaData;
import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;
import com.example.green.domain.file.repository.FileJpaRepository;
import com.example.green.domain.file.utils.ImageValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileService implements FileManager {

	private final StorageHelper storageHelper;
	private final FileJpaRepository fileJpaRepository;
	private final ImageValidator imageValidator;

	public String uploadImage(MultipartFile imageFile, Purpose purpose) {
		imageValidator.validate(imageFile);

		FileMetaData fileMetaData = FileMetaData.from(imageFile);
		String imageKey = storageHelper.generateFileKey(purpose.getValue(), fileMetaData.getExtension());
		FileEntity fileEntity = FileEntity.create(fileMetaData, imageKey, purpose);
		fileJpaRepository.save(fileEntity);

		return storageHelper.uploadImage(imageKey, imageFile);
	}

	@Override
	public void confirmUsingImage(String imageUrl) {
		FileEntity fileEntity = getFileEntityFromImageUrl(imageUrl);
		fileEntity.markAsPermanent();
	}

	@Override
	public void unUseImage(String imageUrl) {
		FileEntity fileEntity = getFileEntityFromImageUrl(imageUrl);
		fileEntity.markDeleted();
	}

	@Override
	public void swapImage(String beforeImageUrl, String afterImageUrl) {
		if (beforeImageUrl.equals(afterImageUrl)) {
			return;
		}
		unUseImage(beforeImageUrl);
		confirmUsingImage(afterImageUrl);
	}

	private FileEntity getFileEntityFromImageUrl(String imageUrl) {
		imageValidator.validateUrl(imageUrl);
		String imageKey = storageHelper.extractImageKey(imageUrl);
		return fileJpaRepository.findByFileKey(imageKey)
			.orElseThrow(() -> new FileException(FileExceptionMessage.NOT_FOUND_FILE));
	}
}

package com.example.green.domain.file.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.config.SystemFileConfig;
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
public class FileService {

	private final StorageHelper storageHelper;
	private final FileJpaRepository fileJpaRepository;
	private final ImageValidator imageValidator;
	private final SystemFileConfig systemFileConfig;

	public String uploadImage(MultipartFile imageFile, Purpose purpose) {
		imageValidator.validate(imageFile);

		FileMetaData fileMetaData = FileMetaData.from(imageFile);
		String imageKey = storageHelper.generateFileKey(purpose.getValue(), fileMetaData.getExtension());
		FileEntity fileEntity = FileEntity.create(fileMetaData, imageKey, purpose);
		fileJpaRepository.save(fileEntity);

		return storageHelper.uploadImage(imageKey, imageFile);
	}

	public void confirmUsingImage(String imageUrl) {
		if (imageUrl == null || imageUrl.trim().isEmpty()) {
			return;
		}
		
		// 시스템 파일은 처리하지 않음
		if (systemFileConfig.isSystemFile(imageUrl)) {
			log.debug("시스템 파일은 상태 변경이 불필요합니다: {}", imageUrl);
			return;
		}
		
		FileEntity fileEntity = getFileEntityFromImageUrl(imageUrl);
		if (fileEntity != null && !fileEntity.isSystemFile()) {
			fileEntity.markAsPermanent();
		}
	}

	public void unUseImage(String imageUrl) {
		if (imageUrl == null || imageUrl.trim().isEmpty()) {
			return;
		}
		
		// 시스템 파일은 삭제할 수 없음
		if (systemFileConfig.isSystemFile(imageUrl)) {
			log.debug("시스템 파일은 삭제할 수 없습니다: {}", imageUrl);
			return;
		}

		FileEntity fileEntity = getFileEntityFromImageUrl(imageUrl);
		if (fileEntity != null && !fileEntity.isSystemFile()) {
			fileEntity.markDeleted();
		}
	}

	public void swapImage(String beforeImageUrl, String afterImageUrl) {
		if (beforeImageUrl == null && afterImageUrl == null) {
			return;
		}
		if (beforeImageUrl != null && beforeImageUrl.equals(afterImageUrl)) {
			return;
		}
		unUseImage(beforeImageUrl);
		confirmUsingImage(afterImageUrl);
	}

	private FileEntity getFileEntityFromImageUrl(String imageUrl) {
		imageValidator.validateUrl(imageUrl);
		String imageKey = storageHelper.extractImageKey(imageUrl);
		return fileJpaRepository.findByFileKey(imageKey)
			.orElse(null);  // 파일이 없으면 null 반환, 기본 이미지 등은 DB에 없을 수 있음
	}
}

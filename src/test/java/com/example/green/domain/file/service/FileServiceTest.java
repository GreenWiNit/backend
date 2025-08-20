package com.example.green.domain.file.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.config.SystemFileConfig;
import com.example.green.domain.file.domain.FileEntity;
import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;
import com.example.green.domain.file.repository.FileJpaRepository;
import com.example.green.domain.file.utils.ImageValidator;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

	@Mock
	private StorageHelper storageHelper;
	@Mock
	private FileJpaRepository fileJpaRepository;
	@Mock
	private ImageValidator imageValidator;
	@Mock
	private SystemFileConfig systemFileConfig;
	@InjectMocks
	private FileService fileService;

	@Test
	void 유효한_이미지_파일로_업로드에_성공하면_이미지_URL을_반환한다() {
		// given
		MultipartFile imageFile = mock(MultipartFile.class);
		createImageStub(imageFile);

		Purpose purpose = mock(Purpose.class);
		String purposeValue = "purposeValue";
		when(purpose.getValue()).thenReturn(purposeValue);

		String imageKey = "imageKey";
		when(storageHelper.generateFileKey(eq(purposeValue), anyString())).thenReturn(imageKey);

		String imageUrl = "imageUrl";
		when(storageHelper.uploadImage(eq(imageKey), eq(imageFile))).thenReturn(imageUrl);

		// when
		String result = fileService.uploadImage(imageFile, purpose);

		// then
		assertThat(result).isEqualTo(imageUrl);
		verify(imageValidator).validate(imageFile);
		verify(fileJpaRepository).save(any(FileEntity.class));
	}

	private static void createImageStub(MultipartFile imageFile) {
		when(imageFile.getOriginalFilename()).thenReturn("test.png");
		when(imageFile.getContentType()).thenReturn("image/png");
		when(imageFile.getSize()).thenReturn(1000L);
	}

	@Test
	void imageUrl의_이미지_정보_사용을_확정한다() {
		// given
		String imageUrl = "imageUrl";
		String imageKey = "imageKey";
		when(systemFileConfig.isSystemFile(imageUrl)).thenReturn(false);
		when(storageHelper.extractImageKey(imageUrl)).thenReturn(imageKey);

		FileEntity fileEntity = mock(FileEntity.class);
		when(fileEntity.isSystemFile()).thenReturn(false);
		when(fileJpaRepository.findByFileKey(eq(imageKey))).thenReturn(Optional.of(fileEntity));

		// when
		fileService.confirmUsingImage(imageUrl);

		// then
		verify(imageValidator).validateUrl(imageUrl);
		verify(fileEntity).markAsPermanent();
	}

	@Test
	void 존재하지_않는_이미지_키_일_경우_아무것도_하지_않는다() {
		// given
		String imageUrl = "imageUrl";
		String imageKey = "imageKey";
		when(systemFileConfig.isSystemFile(imageUrl)).thenReturn(false);
		when(storageHelper.extractImageKey(imageUrl)).thenReturn(imageKey);
		when(fileJpaRepository.findByFileKey(eq(imageKey))).thenReturn(Optional.empty());

		// when
		fileService.confirmUsingImage(imageUrl);

		// then - null 반환으로 예외가 발생하지 않음
		verify(imageValidator).validateUrl(imageUrl);
		verify(storageHelper).extractImageKey(imageUrl);
	}

	@Test
	void 미사용_이미지는_논리적_삭제를_진행한다() {
		// given
		String imageUrl = "imageUrl";
		String imageKey = "imageKey";
		when(systemFileConfig.isSystemFile(imageUrl)).thenReturn(false);
		when(storageHelper.extractImageKey(imageUrl)).thenReturn(imageKey);

		FileEntity fileEntity = mock(FileEntity.class);
		when(fileEntity.isSystemFile()).thenReturn(false);
		when(fileJpaRepository.findByFileKey(eq(imageKey))).thenReturn(Optional.of(fileEntity));

		// when
		fileService.unUseImage(imageUrl);

		// then
		verify(imageValidator).validateUrl(imageUrl);
		verify(fileEntity).markDeleted();
	}
	
	@Test
	void 시스템_파일은_삭제하지_않는다() {
		// given
		String systemImageUrl = "https://static.greenwinit.store/images/profile/default.png";
		when(systemFileConfig.isSystemFile(systemImageUrl)).thenReturn(true);

		// when
		fileService.unUseImage(systemImageUrl);

		// then - 시스템 파일이므로 아무 작업도 하지 않음
		verify(imageValidator, never()).validateUrl(any());
		verify(fileJpaRepository, never()).findByFileKey(any());
	}
	
	@Test
	void 시스템_파일은_상태변경하지_않는다() {
		// given
		String systemImageUrl = "https://static.greenwinit.store/images/profile/default.png";
		when(systemFileConfig.isSystemFile(systemImageUrl)).thenReturn(true);

		// when
		fileService.confirmUsingImage(systemImageUrl);

		// then - 시스템 파일이므로 아무 작업도 하지 않음
		verify(imageValidator, never()).validateUrl(any());
		verify(fileJpaRepository, never()).findByFileKey(any());
	}
}
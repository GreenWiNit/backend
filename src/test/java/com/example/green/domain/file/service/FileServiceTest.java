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

import com.example.green.domain.file.domain.FileEntity;
import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;
import com.example.green.domain.file.outport.StorageHelper;
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
	@InjectMocks
	private FileService fileService;

	@Test
	void 유효한_이미지_파일로_업로드에_성공하면_이미지_URL을_반환한다() {
		// given
		MultipartFile imageFile = mock(MultipartFile.class);
		when(imageFile.getOriginalFilename()).thenReturn("test.png");
		when(imageFile.getContentType()).thenReturn("image/png");
		when(imageFile.getSize()).thenReturn(1000L);

		Purpose purpose = mock(Purpose.class);
		String purposeValue = "purposeValue";
		when(purpose.getValue()).thenReturn(purposeValue);

		String imageKey = "imageKey";
		when(storageHelper.generateFileKey(eq(purposeValue), anyString())).thenReturn(imageKey);

		String imageUrl = "imageUrl";
		when(storageHelper.getFullImageUrl(eq(imageKey))).thenReturn(imageUrl);

		// when
		String result = fileService.uploadImage(imageFile, purpose);

		// then
		assertThat(result).isEqualTo(imageUrl);
		verify(imageValidator).validate(imageFile);
		verify(fileJpaRepository).save(any(FileEntity.class));
		verify(storageHelper).uploadImage(imageKey, imageFile);
	}

	@Test
	void 이미지_파일_업로드_중_에러가_발생하면_예외를_떨어트린다() {
		// given
		MultipartFile imageFile = mock(MultipartFile.class);
		when(imageFile.getOriginalFilename()).thenReturn("test.png");
		when(imageFile.getContentType()).thenReturn("image/png");
		when(imageFile.getSize()).thenReturn(1000L);

		Purpose purpose = mock(Purpose.class);
		String purposeValue = "purposeValue";
		when(purpose.getValue()).thenReturn(purposeValue);

		String imageKey = "imageKey";
		when(storageHelper.generateFileKey(eq(purposeValue), anyString())).thenReturn(imageKey);

		doThrow(IllegalArgumentException.class).when(storageHelper).uploadImage(imageKey, imageFile);

		// when & then
		assertThatThrownBy(() -> fileService.uploadImage(imageFile, purpose))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.IMAGE_UPLOAD_FAILED);

		verify(fileJpaRepository).save(any(FileEntity.class));
		verify(imageValidator).validate(imageFile);
	}

	@Test
	void imageUrl의_이미지_정보_사용을_확정한다() {
		// given
		String imageUrl = "imageUrl";
		String imageKey = "imageKey";
		when(storageHelper.extractImageKey(imageUrl)).thenReturn(imageKey);

		FileEntity fileEntity = mock(FileEntity.class);
		when(fileJpaRepository.findByFileKey(eq(imageKey))).thenReturn(Optional.of(fileEntity));

		// when
		fileService.confirmUsingImage(imageUrl);

		// then
		verify(imageValidator).validateUrl(imageUrl);
		verify(fileEntity).markAsPermanent();
	}

	@Test
	void imageUrl에서_이미지_키_추출_중_버그가_발생하면_예외를_던진다() {
		// given
		String imageUrl = "imageUrl";
		doThrow(IllegalArgumentException.class).when(storageHelper).extractImageKey(imageUrl);

		// when & then
		assertThatThrownBy(() -> fileService.confirmUsingImage(imageUrl))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.INVALID_IMAGE_URL);

		verify(imageValidator).validateUrl(imageUrl);
	}

	@Test
	void 존재하지_않는_이미지_키_일_경우_예외를_던진다() {
		// given
		String imageUrl = "imageUrl";
		String imageKey = "imageKey";
		when(storageHelper.extractImageKey(imageUrl)).thenReturn(imageKey);
		when(fileJpaRepository.findByFileKey(eq(imageKey))).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> fileService.confirmUsingImage(imageUrl))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.NOT_FOUND_FILE);

		verify(imageValidator).validateUrl(imageUrl);
	}

	@Test
	void 미사용_이미지는_논리적_삭제를_진행한다() {
		// given
		String imageUrl = "imageUrl";
		String imageKey = "imageKey";
		when(storageHelper.extractImageKey(imageUrl)).thenReturn(imageKey);

		FileEntity fileEntity = mock(FileEntity.class);
		when(fileJpaRepository.findByFileKey(eq(imageKey))).thenReturn(Optional.of(fileEntity));

		// when
		fileService.unUseImage(imageUrl);

		// then
		verify(imageValidator).validateUrl(imageUrl);
		verify(fileEntity).markDeleted();
	}
}
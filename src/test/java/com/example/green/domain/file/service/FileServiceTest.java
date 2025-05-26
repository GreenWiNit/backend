package com.example.green.domain.file.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
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
import com.example.green.domain.file.utils.FileKeyGenerator;
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
	private FileKeyGenerator fileKeyGenerator;
	@InjectMocks
	private FileService fileService;

	@Mock
	private MultipartFile imageFile;

	@BeforeEach
	void setUp() {
		when(imageFile.getOriginalFilename()).thenReturn("test.png");
		when(imageFile.getContentType()).thenReturn("image/png");
		when(imageFile.getSize()).thenReturn(1000L);
	}

	@Test
	void 유효한_이미지_파일로_업로드에_성공하면_이미지_URL을_반환한다() {
		// given
		Purpose purpose = mock(Purpose.class);
		String purposeValue = "purposeValue";
		when(purpose.getValue()).thenReturn(purposeValue);

		String imageKey = "imageKey";
		when(fileKeyGenerator.generate(eq(purposeValue), anyString())).thenReturn(imageKey);

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
		Purpose purpose = mock(Purpose.class);
		String purposeValue = "purposeValue";
		when(purpose.getValue()).thenReturn(purposeValue);

		String imageKey = "imageKey";
		when(fileKeyGenerator.generate(eq(purposeValue), anyString())).thenReturn(imageKey);

		doThrow(RuntimeException.class).when(storageHelper).uploadImage(imageKey, imageFile);

		// when & then
		assertThatThrownBy(() -> fileService.uploadImage(imageFile, purpose))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.IMAGE_UPLOAD_FAILED);

		verify(fileJpaRepository).save(any(FileEntity.class));
		verify(imageValidator).validate(imageFile);
	}
}
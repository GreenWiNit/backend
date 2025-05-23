package com.example.green.domain.file.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;
import com.example.green.domain.file.outport.StorageHelper;
import com.example.green.domain.file.utils.FileKeyGenerator;
import com.example.green.domain.file.utils.ImageValidator;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

	@Mock
	private StorageHelper storageHelper;
	@Mock
	private ImageValidator imageValidator;
	@Mock
	private FileKeyGenerator fileKeyGenerator;
	@InjectMocks
	private FileService fileService;

	@Test
	void 유효한_이미지_파일로_업로드에_성공하면_이미지_URL을_반환한다() {
		// given
		MultipartFile imageFile = mock(MultipartFile.class);
		when(imageFile.getOriginalFilename()).thenReturn("image.jpg");
		when(fileKeyGenerator.generate("domain", "image.jpg")).thenReturn("imageKey");
		when(storageHelper.getFullImageUrl("imageKey")).thenReturn("imageUrl");

		// when
		String result = fileService.uploadImage(imageFile);

		// then
		assertThat(result).isEqualTo("imageUrl");
		verify(imageValidator).validateImageFile(imageFile);
		verify(storageHelper).uploadImage("imageKey", imageFile);
	}

	@Test
	void 이미지_파일_업로드_중_에러가_발생하면_예외를_떨어트린다() {
		// given
		MultipartFile imageFile = mock(MultipartFile.class);
		when(imageFile.getOriginalFilename()).thenReturn("image.jpg");
		when(fileKeyGenerator.generate("domain", "image.jpg")).thenReturn("imageKey");
		doThrow(RuntimeException.class).when(storageHelper).uploadImage("imageKey", imageFile);

		// when & then
		assertThatThrownBy(() -> fileService.uploadImage(imageFile))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.IMAGE_UPLOAD_FAILED);
		verify(imageValidator).validateImageFile(imageFile);
	}
}
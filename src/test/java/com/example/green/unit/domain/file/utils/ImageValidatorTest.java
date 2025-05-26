package com.example.green.unit.domain.file.utils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;
import com.example.green.domain.file.utils.ImageValidator;

@ExtendWith(MockitoExtension.class)
class ImageValidatorTest {

	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

	@Mock
	private MultipartFile multipartFile;
	private ImageValidator imageValidator;

	@BeforeEach
	void setUp() {
		DataSize maxImageSize = DataSize.ofBytes(MAX_FILE_SIZE);
		imageValidator = new ImageValidator(maxImageSize);
	}

	@ParameterizedTest
	@MethodSource("invalidMultipartFiles")
	void 이미지_파일이_null이거나_비어있으면_예외가_발생한다(MultipartFile multipartFile) {
		// when & then
		assertThatThrownBy(() -> imageValidator.validate(multipartFile))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.REQUIRED_IMAGE_FILE);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 이미지_파일_이름이_비어있거나_null일_경우_검증이_실패한다(String originalFilename) {
		// given
		when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);

		// when & then
		assertThatThrownBy(() -> imageValidator.validate(multipartFile))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.EMPTY_IMAGE_FILE_NAME);
	}

	@ParameterizedTest
	@ValueSource(strings = {"executor.exe", "text.txt", "disk-image.dmg", "no-extension"})
	void 이미지_관련_확장자를_가진_파일이_아닐_경우_검증이_실패한다(String originalFilename) {
		// given
		when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);

		// when & then
		assertThatThrownBy(() -> imageValidator.validate(multipartFile))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.INVALID_IMAGE_TYPE);
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"text/..", "application/..", "multipart/..", "audio/..", "text/..", "image/bmp"})
	void 허용된_이미지_content_type이_아니라면_검증이_실패한다(String contentType) {
		// given
		when(multipartFile.getOriginalFilename()).thenReturn("validImage.jpeg");
		when(multipartFile.getContentType()).thenReturn(contentType);

		// when & then
		assertThatThrownBy(() -> imageValidator.validate(multipartFile))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.INVALID_IMAGE_TYPE);
	}

	@Test
	void 이미지_파일의_사이즈가_최대_허용_범위를_초과하면_검증이_실패한다() {
		// given
		when(multipartFile.getOriginalFilename()).thenReturn("validImage.jpeg");
		when(multipartFile.getContentType()).thenReturn("image/jpeg");
		when(multipartFile.getSize()).thenReturn(MAX_FILE_SIZE + 1);

		// when & then
		assertThatThrownBy(() -> imageValidator.validate(multipartFile))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.OVER_MAX_IMAGE_SIZE);
	}

	@ParameterizedTest
	@CsvSource(value = {
		"test.jpg-image/jpeg-0", "test.jpeg-image/jpeg-1", "test.png-image/png-2"
	}, delimiter = '-')
	void 유효한_이미지_파일은_검증에_성공한다(String originalFilename, String contentType, long size) {
		// given
		when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
		when(multipartFile.getContentType()).thenReturn(contentType);
		when(multipartFile.getSize()).thenReturn(size);

		// when
		assertThatCode(() -> imageValidator.validate(multipartFile))
			.doesNotThrowAnyException();
	}

	static Stream<Arguments> invalidMultipartFiles() {
		MultipartFile emptyFile = mock(MultipartFile.class);
		when(emptyFile.isEmpty()).thenReturn(true);

		return Stream.of(
			Arguments.of((MultipartFile)null),
			Arguments.of(emptyFile)
		);
	}
}

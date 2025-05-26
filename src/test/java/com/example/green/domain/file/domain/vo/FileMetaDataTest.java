package com.example.green.domain.file.domain.vo;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;

class FileMetaDataTest {

	private MultipartFile multipartFile;

	@BeforeEach
	void setUp() {
		multipartFile = mock(MultipartFile.class);
	}

	@Test
	void 파일_메타_정보가_생성된다() {
		// given
		String originalFilename = "test.png";
		String contentType = "image/png";
		long contentLength = 1000L;
		injectMultipartFile(originalFilename, contentType, contentLength);

		// when
		FileMetaData imageFileMetaData = FileMetaData.from(multipartFile);

		// then
		assertThat(imageFileMetaData.getOriginalFilename()).isEqualTo(originalFilename);
		assertThat(imageFileMetaData.getContentType()).isEqualTo(contentType);
		assertThat(imageFileMetaData.getContentLength()).isEqualTo(contentLength);
	}

	@ParameterizedTest
	@ValueSource(strings = {"       "})
	@NullAndEmptySource
	void 파일_메타데이터의의_원본_이름은_null이거나_공백일_수_없다(String originalFilename) {
		// given
		String contentType = "image/png";
		long contentLength = 1000L;
		injectMultipartFile(originalFilename, contentType, contentLength);

		// when & then
		assertThatThrownBy(() -> FileMetaData.from(multipartFile))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.INVALID_FILE_METADATA);
	}

	@ParameterizedTest
	@ValueSource(strings = {"       "})
	@NullAndEmptySource
	void 파일_메타데이터의의_콘텐츠_타입은_null이거나_공백일_수_없다(String contentType) {
		// given
		String originalFilename = "test.png";
		long contentLength = 1000L;
		injectMultipartFile(originalFilename, contentType, contentLength);

		// when & then
		assertThatThrownBy(() -> FileMetaData.from(multipartFile))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.INVALID_FILE_METADATA);
	}

	@ParameterizedTest
	@ValueSource(longs = {0L, -1L})
	void 파일_메타데이터의의_콘텐츠_길이는_양수여야한다(Long contentLength) {
		// given
		String originalFilename = "test.png";
		String contentType = "image/png";
		injectMultipartFile(originalFilename, contentType, contentLength);

		// when & then
		assertThatThrownBy(() -> FileMetaData.from(multipartFile))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.INVALID_FILE_METADATA);
	}

	@Test
	void 파일_메타데이터에서_확장자를_가져올_수_있다() {
		// given
		String originalFilename = "test.png";
		String contentType = "image/png";
		long contentLength = 1000L;
		injectMultipartFile(originalFilename, contentType, contentLength);

		// when
		FileMetaData imageFileMetaData = FileMetaData.from(multipartFile);

		// then
		assertThat(imageFileMetaData.getExtension()).isEqualTo(".png");
	}

	@ParameterizedTest
	@ValueSource(strings = {"test", ".env", "test."})
	void 확장자가_없거나_dot로_시작하거나_dot로_끝나면_빈_확장자를_반환한다(String originalFilename) {
		// given
		String contentType = "image/png";
		long contentLength = 1000L;
		injectMultipartFile(originalFilename, contentType, contentLength);

		// when
		FileMetaData imageFileMetaData = FileMetaData.from(multipartFile);

		// then
		assertThat(imageFileMetaData.getExtension()).isEmpty();
	}

	private void injectMultipartFile(String originalFilename, String contentType, long contentLength) {
		when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
		when(multipartFile.getContentType()).thenReturn(contentType);
		when(multipartFile.getSize()).thenReturn(contentLength);
	}
}
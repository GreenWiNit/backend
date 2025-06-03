package com.example.integration.file;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;
import com.example.green.domain.file.service.FileService;
import com.example.integration.common.ServiceIntegrationTest;

public class FileServiceIntTest extends ServiceIntegrationTest {

	@Autowired
	private FileService fileService;
	@Autowired
	private FileTestUtil fileTestUtil;

	@BeforeEach
	void setUp() {
		fileTestUtil.createBucket();
	}

	@AfterEach
	void tearDown() {
		fileTestUtil.clearBucket();
	}

	@Test
	void 이미지_파일을_업로드하면_db와_storage에_저장된다() {
		// given
		MultipartFile imageFile = createMockFile("image1.jpg", "image/jpeg");

		// when
		String imageUrl = fileService.uploadImage(imageFile, Purpose.PROFILE);

		// then
		assertThat(fileTestUtil.existsImageInS3(imageUrl)).isTrue();
		assertThat(fileTestUtil.existsInImageMetadataInDb(imageUrl)).isTrue();
	}

	@Test
	void 이미지_업로드_중_예외가_발생하면_db에_저장된_파일이_롤백된다() {
		// given
		MultipartFile imageFile = createMockFile("image.png", "image/png");
		fileTestUtil.clearBucket();

		// when & then
		assertThatThrownBy(() -> fileService.uploadImage(imageFile, Purpose.PROFILE))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.IMAGE_UPLOAD_FAILED);

		assertThat(fileTestUtil.countInImageMetadataInDb()).isZero();
	}

	private static MultipartFile createMockFile(String originalName, String contentType) {
		return new MockMultipartFile("image", originalName, contentType, "imageFile".getBytes());
	}
}

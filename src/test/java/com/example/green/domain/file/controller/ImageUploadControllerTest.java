package com.example.green.domain.file.controller;

import static com.example.green.domain.file.controller.message.ImageResponseMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.service.FileService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.template.base.BaseControllerUnitTest;
import com.example.green.template.request.ImageUploadRequest;

@WebMvcTest(ImageUploadController.class)
class ImageUploadControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private FileService fileService;

	@Test
	void 이미지_업로드_요청이_성공한다() {
		// given
		String imageKey = "imageKey";
		String filename = "filename.jpg";
		byte[] bytes = "test image content".getBytes();
		String contentType = "image/jpeg";
		String purpose = "challenge";
		when(fileService.uploadImage(any(MultipartFile.class), any(Purpose.class))).thenReturn(imageKey);

		// when
		ApiTemplate<String> response = ImageUploadRequest.upload(filename, bytes, contentType, purpose);

		// then
		assertThat(response.message()).isEqualTo(IMAGE_UPLOAD_SUCCESS.getMessage());
		assertThat(response.result()).isEqualTo(imageKey);
	}
}
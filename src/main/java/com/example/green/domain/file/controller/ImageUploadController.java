package com.example.green.domain.file.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.controller.docs.ImageUploadControllerDocs;
import com.example.green.domain.file.controller.message.ImageResponseMessage;
import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.service.FileService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.annotation.PublicApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/images")
public class ImageUploadController implements ImageUploadControllerDocs {

	private final FileService fileService;

	@PublicApi
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiTemplate<String> upload(
		@Valid @RequestParam MultipartFile imageFile,
		@Valid @RequestParam Purpose purpose
	) {
		String result = fileService.uploadImage(imageFile, purpose);
		return ApiTemplate.ok(ImageResponseMessage.IMAGE_UPLOAD_SUCCESS, result);
	}
}

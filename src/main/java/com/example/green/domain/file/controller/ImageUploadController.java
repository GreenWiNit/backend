package com.example.green.domain.file.controller;

import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.controller.docs.ImageUploadControllerDocs;
import com.example.green.domain.file.controller.message.ImageResponseMessage;
import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.service.FileService;
import com.example.green.global.api.ApiTemplate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImageUploadController implements ImageUploadControllerDocs {

	private final FileService fileService;

	public ApiTemplate<String> upload(MultipartFile imageFile, Purpose purpose) {
		String result = fileService.uploadImage(imageFile, purpose);
		return ApiTemplate.ok(ImageResponseMessage.IMAGE_UPLOAD_SUCCESS, result);
	}
}

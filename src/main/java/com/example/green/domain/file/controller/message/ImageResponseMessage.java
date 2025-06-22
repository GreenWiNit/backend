package com.example.green.domain.file.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImageResponseMessage implements ResponseMessage {
	IMAGE_UPLOAD_SUCCESS("이미지 업로드에 성공했습니다.");

	private final String message;
}

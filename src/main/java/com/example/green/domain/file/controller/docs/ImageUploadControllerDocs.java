package com.example.green.domain.file.controller.docs;

import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "이미지 API", description = "이미지 관련 API 목록입니다.")
public interface ImageUploadControllerDocs {

	@Operation(summary = "이미지 업로드", description = "이미지를 업로드 합니다.")
	@ApiResponse(responseCode = "200", description = "이미지 업로드에 성공했습니다.")
	@ApiResponse(
		responseCode = "500",
		description = "이미지 파일 추출에 실패했습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiResponse(
		responseCode = "502",
		description = "이미지 업로드에 실패했습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<String> upload(
		@Schema(description = "이미지 파일")
		MultipartFile imageFile,
		@Schema(description = "이미지 업로드 목적")
		Purpose purpose
	);
}

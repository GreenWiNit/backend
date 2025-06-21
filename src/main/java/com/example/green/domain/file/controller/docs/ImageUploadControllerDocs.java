package com.example.green.domain.file.controller.docs;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "이미지 API", description = "이미지 관련 API 목록입니다.")
@RestController
@RequestMapping("/api/images")
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
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ApiTemplate<String> upload(@Valid @RequestParam MultipartFile imageFile, @Valid @RequestParam Purpose purpose);
}

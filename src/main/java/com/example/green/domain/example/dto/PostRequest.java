package com.example.green.domain.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "게시글 생성/수정 요청 DTO")
public class PostRequest {

	@NotBlank(message = "제목은 필수입니다.")
	@Size(min = 5, max = 100, message = "제목은 5자 이상 20자 이하로 입력해주세요.")
	@Schema(description = "게시글 제목", example = "오늘의 공부 일지")
	private String title;

	@NotBlank(message = "내용은 필수입니다.")
	@Size(min = 10, max = 1000, message = "내용은 10자 이상 1000자 이하로 입력해주세요.")
	@Schema(description = "게시글 내용", example = "오늘은 Swagger 문서화에 대해 배웠다.")
	private String content;

	@Min(value = 1, message = "챌린지 ID는 1이상의 숫자여야 합니다.")
	@Schema(description = "챌린지 ID", example = "2")
	private Long challengeId;
}

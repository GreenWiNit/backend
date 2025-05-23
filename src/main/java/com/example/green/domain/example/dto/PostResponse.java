package com.example.green.domain.example.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "게시글 응답 DTO")
public class PostResponse {

	@Schema(description = "게시글 ID", example = "1")
	private Long id;

	@Schema(description = "게시글 제목", example = "오늘의 공부 일지")
	private String title;

	@Schema(description = "게시글 내용", example = "오늘은 Swagger 문서화에 대해 배웠다.")
	private String content;

	@Schema(description = "작성자", example = "홍길동")
	private String author;

	@Schema(description = "작성일시", example = "2024-05-18T12:30:00")
	private LocalDateTime createdAt;
}
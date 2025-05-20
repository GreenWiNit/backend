package com.example.green.domain.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "게시글 목록 응답 DTO")
public class PostListResponse {

	@Schema(description = "게시글 목록")
	private List<PostResponse> content;

	@Schema(description = "총 페이지 수", example = "10")
	private int totalPages;

	@Schema(description = "전체 게시글 수", example = "153")
	private long totalElements;

	@Schema(description = "현재 페이지 번호", example = "0")
	private int page;

	@Schema(description = "페이지 크기", example = "20")
	private int size;
}

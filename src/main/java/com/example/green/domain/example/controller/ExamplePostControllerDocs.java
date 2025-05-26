package com.example.green.domain.example.controller;

import com.example.green.domain.example.dto.PostListResponse;
import com.example.green.domain.example.dto.PostRequest;
import com.example.green.domain.example.dto.PostResponse;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.docs.ApiError400;
import com.example.green.global.docs.ApiErrorStandard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "게시글 API", description = "게시글 생성, 조회, 수정, 삭제 API")
public interface ExamplePostControllerDocs {

	@Operation(summary = "게시글 생성", description = "게시글을 생성합니다.")
	@ApiErrorStandard
	@ApiError400
	@ApiResponse(responseCode = "200", description = "게시글 생성 성공", useReturnTypeSchema = true)
	ApiTemplate<PostResponse> createPost(PostRequest request);

	@Operation(summary = "게시글 수정", description = "게시글 ID로 게시글을 수정합니다.")
	@ApiErrorStandard
	@ApiError400
	@ApiResponse(responseCode = "200", description = "게시글 수정 성공", useReturnTypeSchema = true)
	ApiTemplate<PostResponse> updatePost(
		@Parameter(name = "postId", description = "수정할 게시글의 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long postId,
		PostRequest request
	);

	@Operation(summary = "게시글 삭제", description = "게시글 ID로 게시글을 삭제합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "게시글 삭제 성공", useReturnTypeSchema = true)
	NoContent deletePost(
		@Parameter(name = "postId", description = "삭제할 게시글의 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long postId
	);

	@Operation(summary = "게시글 조회", description = "게시글 ID로 게시글을 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "게시글 조회 성공", useReturnTypeSchema = true)
	ApiTemplate<PostResponse> getPostById(
		@Parameter(name = "postId", description = "조회할 게시글의 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long postId
	);

	@Operation(summary = "게시글 목록 조회", description = "페이징 정보를 기준으로 게시글 목록을 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공", useReturnTypeSchema = true)
	ApiTemplate<PostListResponse> getPosts(
		@Parameter(name = "page", description = "조회할 페이지 번호 (0부터 시작)", example = "0", required = true) int page,
		@Parameter(name = "size", description = "페이지당 게시글 수", example = "20", required = true) int size
	);
}

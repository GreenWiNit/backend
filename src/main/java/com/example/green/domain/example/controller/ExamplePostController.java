package com.example.green.domain.example.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.example.api.ExamplePostResponseMessage;
import com.example.green.domain.example.dto.PostListResponse;
import com.example.green.domain.example.dto.PostRequest;
import com.example.green.domain.example.dto.PostResponse;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.security.annotation.AdminApi;
import com.example.green.global.security.annotation.AuthenticatedApi;
import com.example.green.global.security.annotation.PublicApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class ExamplePostController implements ExamplePostControllerDocs {

	@AuthenticatedApi(reason = "게시글 작성은 로그인한 사용자만 가능합니다")
	@PostMapping
	public ApiTemplate<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
		PostResponse response = new PostResponse(1L, request.getTitle(), request.getContent(), "홍길동",
			LocalDateTime.now());
		return ApiTemplate.ok(ExamplePostResponseMessage.POST_CREATED, response);
	}

	@AuthenticatedApi(reason = "게시글 수정은 로그인한 사용자만 가능합니다")
	@PutMapping("/{postId}")
	public ApiTemplate<PostResponse> updatePost(@PathVariable Long postId, @Valid @RequestBody PostRequest request) {
		PostResponse response = new PostResponse(postId, request.getTitle(), request.getContent(), "홍길동",
			LocalDateTime.now());
		return ApiTemplate.ok(ExamplePostResponseMessage.POST_UPDATED, response);
	}

	@AdminApi(reason = "게시글 삭제는 관리자만 가능합니다")
	@DeleteMapping("/{postId}")
	public NoContent deletePost(@PathVariable Long postId) {
		return NoContent.ok(ExamplePostResponseMessage.POST_DELETED);
	}

	@PublicApi(reason = "게시글 상세 조회는 누구나 가능합니다")
	@GetMapping("/{postId}")
	public ApiTemplate<PostResponse> getPostById(@PathVariable Long postId) {
		PostResponse response = new PostResponse(postId, "제목 예시", "본문 예시", "홍길동", LocalDateTime.now());
		return ApiTemplate.ok(ExamplePostResponseMessage.POST_FOUND, response);
	}

	@PublicApi(reason = "게시글 목록 조회는 누구나 가능합니다")
	@GetMapping
	public ApiTemplate<PostListResponse> getPosts(@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size) {
		List<PostResponse> posts = List.of(
			new PostResponse(1L, "첫 번째 글", "내용입니다", "홍길동", LocalDateTime.now()),
			new PostResponse(2L, "두 번째 글", "내용입니다", "김철수", LocalDateTime.now())
		);
		PostListResponse response = new PostListResponse(posts, 10, 153, page, size);
		return ApiTemplate.ok(ExamplePostResponseMessage.POST_FOUND, response);
	}
}
